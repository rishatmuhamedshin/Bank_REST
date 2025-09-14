package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import jakarta.validation.Valid;

public interface TransferService {
    TransferResponse transferBalance(@Valid TransferRequest request, String username);
}
