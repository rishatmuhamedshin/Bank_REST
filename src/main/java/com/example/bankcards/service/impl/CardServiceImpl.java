package com.example.bankcards.service.impl;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enumeration.BlockRequestStatus;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.exception.exceptions.*;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.bankcards.util.CardEncryptUtil.maskCardNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardBlockRequestRepository blockRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardDto> getAllCards(PageRequest pageRequest) {
        Page<BankCard> page = cardRepository.findAll(pageRequest);
        return getCardDtoPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardDto> getUserCards(String username, CardSearchRequest cardSearchRequest) {
        Sort sort = Sort.by(
                cardSearchRequest.getSortDirection().equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                cardSearchRequest.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                cardSearchRequest.getPage(),
                cardSearchRequest.getSize(),
                sort
        );

        Specification<BankCard> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("username"), username));
            predicates.add(cb.equal(root.get("status"), CardStatus.ACTIVE));

            String searchQuery = cardSearchRequest.getSearchQuery();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likePattern = "%" + searchQuery.toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("cardNumber")), likePattern));
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<BankCard> page = cardRepository.findAll(spec, pageable);


        return getCardDtoPageResponse(page);
    }

    @Override
    @Transactional
    public CardDto createNewCard(CreateCardRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new UserNotFoundException("Пользователя с таким именем не существует"));

        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new CardNumberExistsException("Карта с таким номером существует");
        }
        var card = BankCard.builder()
                .cardNumber(request.getCardNumber())
                .balance(request.getBalance())
                .user(user)
                .expiryDate(request.getExpiryDate())
                .status(request.getCardStatus())
                .build();

        cardRepository.save(card);

        log.info("Создание новой карты {}", maskCardNumber(card.getCardNumber()));
        return convertToDto(card);
    }

    @Transactional
    public void processBlockRequest(BlockRequestDto requestDto, Long cardId) {
        var request = blockRequestRepository.findByCardId(cardId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос не найден"));

        if (request.getStatus() != BlockRequestStatus.PENDING) {
            throw new CardOperationException("Запрос уже обработан");
        }

        BankCard card = request.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        request.setStatus(BlockRequestStatus.APPROVED);
        request.setReason(requestDto.getReason());

        log.info("Создание нового запроса на блокировку:{}", request.getCreatedAt());
        blockRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void createBlockRequest(BlockRequestDto requestDto, String username, Long cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Карта не принадлежит пользователю");
        }

        if (blockRequestRepository.existsByCardAndUserAndStatus(
                card, user, BlockRequestStatus.PENDING)) {
            throw new CardOperationException("Запрос на блокировку этой карты уже отправлен");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException("Карта уже заблокирована");
        }

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new CardOperationException("Карта с истекшим сроком действия");
        }

        CardBlockRequest request = new CardBlockRequest();
        request.setCard(card);
        request.setUser(user);
        request.setReason(requestDto.getReason());
        request.setStatus(BlockRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        log.info("Создание нового запроса на блокировку:{}", request.getCreatedAt());
        blockRequestRepository.save(request);
    }

    @Override
    @Transactional
    public CardDto activateCard(Long cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new CardStatusException("Карта уже активирована");
        }
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardOperationException("Срок карты истек");
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);

        log.info("Активация карты:{}", maskCardNumber(card.getCardNumber()));

        return convertToDto(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        log.info("Удаление карты:{}", maskCardNumber(card.getCardNumber()));

        cardRepository.delete(card);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public BalanceInfoDto getCardBalance(Long cardId, String username) {
        var card = cardRepository.findBalanceByCardIdAndUsername(cardId, username)
                .orElseThrow(() -> {
                    if (!cardRepository.existsById(cardId)) {
                        return new CardNotFoundException("Карта не найдена");
                    }
                    return new UserAccessException("Карта вам не принадлежит");
                });
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardStatusException("Карта не активна");
        }

        return convertToBalanceInfoDto(card);

    }

    private PageResponse<CardDto> getCardDtoPageResponse(Page<BankCard> page) {
        var content = page.getContent().stream().map(this::convertToDto).toList();

        var metadata = new PageResponse.Metadata();
        metadata.setPage(page.getNumber());
        metadata.setSize(page.getSize());
        metadata.setTotalElements(page.getTotalElements());
        metadata.setTotalPages(page.getTotalPages());

        PageResponse<CardDto> response = new PageResponse<>();
        response.setContent(content);
        response.setMetadata(metadata);

        return response;
    }

    //Здесь можно MapStruct, но в стеке технологий он не указан
    private CardDto convertToDto(BankCard card) {
        return CardDto.builder()
                .id(card.getId())
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .cardHolderName(card.getUser().getUsername())
                .build();
    }

    private BalanceInfoDto convertToBalanceInfoDto(BankCard card) {
        return BalanceInfoDto.builder()
                .maskedCardNumber(maskCardNumber(card.getCardNumber()))
                .expiryDate(card.getExpiryDate())
                .cardHolderName(card.getUser().getUsername())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }

}
