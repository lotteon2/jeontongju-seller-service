package com.jeontongju.seller.service;

import com.jeontongju.seller.domain.Seller;
import com.jeontongju.seller.dto.reqeust.ModifySellerInfo;
import com.jeontongju.seller.dto.reqeust.SellerJudgeRequestDto;
import com.jeontongju.seller.dto.response.*;
import com.jeontongju.seller.dto.response.SellerInfoForConsumerDto;
import com.jeontongju.seller.enums.ApprovalState;
import com.jeontongju.seller.exception.SellerEntityNotFoundException;
import com.jeontongju.seller.kafka.SellerProducer;
import com.jeontongju.seller.mapper.SellerMapper;
import com.jeontongju.seller.repository.SellerRepository;
import io.github.bitbox.bitbox.dto.SellerInfoDto;
import io.github.bitbox.bitbox.dto.SellerInfoForAuctionDto;
import io.github.bitbox.bitbox.dto.SignUpInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

  private final SellerRepository sellerRepository;
  private final SellerProducer sellerProducer;
  private final SellerMapper sellerMapper;

  public SellerInfoDto getSellerInfo(Long sellerId) {

    return sellerMapper.toSellerInfoDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  public SellerMyInfoDto getMySellerInfo(Long sellerId) {

    return SellerMyInfoDto.toDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  public Page<SellerInfoForAdminDto> getAllSeller(Pageable pageable) {

    return sellerRepository.findAllSeller(pageable);
  }

  public Page<GetSellerForConsumerDto> getAllSellerByConsumer(Pageable pageable) {

    return sellerRepository.findAllSellerByConsumer(pageable);
  }

  @Transactional
  public void modifySellerApprovalState(SellerJudgeRequestDto sellerJudgeRequestDto) {
    Seller seller =
        sellerRepository
            .findById(sellerJudgeRequestDto.getSellerId())
            .orElseThrow(SellerEntityNotFoundException::new);
    seller.setApprovalState(sellerJudgeRequestDto.getApprovalState());
  }

  public SellerInfoForConsumerDto getSellerOneForConsumer(Long sellerId) {

    return SellerInfoForConsumerDto.toDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  public SellerInfoDetailsDto getSellerOne(Long sellerId) {

    return SellerInfoDetailsDto.toDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  public GetMySellerInfo getMyInfo(Long sellerId) {

    return GetMySellerInfo.toDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  @Transactional
  public void deleteSeller(Long sellerId) {
    Seller seller =
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new);
    seller.setDeleted(true);
    sellerProducer.deleteSellerToProduct(sellerId);
    sellerProducer.deleteSellerToReview(sellerId);
    sellerProducer.deleteSellerToAuthentication(sellerId);
  }

  @Transactional
  public void modifySeller(Long memberId, ModifySellerInfo modifySellerInfo) {

    Seller seller =
        sellerRepository.findById(memberId).orElseThrow(SellerEntityNotFoundException::new);
    seller.modifySeller(modifySellerInfo);

    sellerProducer.sendUpdateSeller(sellerMapper.toSellerInfoDto(seller));
  }

  @Transactional
  public Seller saveSeller(SignUpInfo signUpInfo) {
    return sellerRepository.save(sellerMapper.toSeller(signUpInfo));
  }

  public SellerInfoForAuctionDto getSellerInfoForAuction(Long sellerId) {

    return sellerMapper.toSellerInfoForAuctionDto(
        sellerRepository.findById(sellerId).orElseThrow(SellerEntityNotFoundException::new));
  }

  public List<GetSellerByAdminDto> getSellerListByAdmin() {

    return sellerRepository.findAll().stream()
        .map(seller -> GetSellerByAdminDto.toDto(seller))
        .collect(Collectors.toList());
  }

  public Long getApprovalWaitCount() {
    return sellerRepository.countByApprovalState(ApprovalState.WAIT);
  }
}
