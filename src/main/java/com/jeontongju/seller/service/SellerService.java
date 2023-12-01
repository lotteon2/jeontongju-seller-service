package com.jeontongju.seller.service;

import com.jeontongju.seller.dto.temp.SellerInfoDto;
import com.jeontongju.seller.exception.SellerEntityNotFoundException;
import com.jeontongju.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

  private final SellerRepository sellerRepository;

  public SellerInfoDto getSellerInfo(Long sellerId) {

    return SellerInfoDto.toDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }
}