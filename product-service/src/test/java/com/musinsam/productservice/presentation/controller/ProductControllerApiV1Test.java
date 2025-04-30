//package com.musinsam.productservice.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.musinsam.productservice.application.dto.request.ReqProductPatchByProductIdDtoApiV1;
//import com.musinsam.productservice.application.dto.request.ReqProductPostDtoApiV1;
//import com.musinsam.productservice.application.dto.request.ReqProductPostDtoApiV1.Product;
//import com.musinsam.productservice.application.dto.request.ReqProductPutByProductIdDtoApiV1;
//import com.musinsam.productservice.application.dto.request.ReqProductPutByProductIdDtoApiV1.Product.Image;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.UUID;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("dev")
//class ProductControllerApiV1Test {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//
//  @Test
//  @DisplayName("상품 생성 성공")
//  public void testProductPostSuccess() throws Exception {
//
//    UUID shopId = UUID.randomUUID();
//
//    ReqProductPostDtoApiV1 dto = ReqProductPostDtoApiV1.builder()
//        .product(Product.builder()
//            .name("testProduct1")
//            .price(BigDecimal.valueOf(12000))
//            .stock(100)
//            .shopId(shopId)
//            .build())
//        .build();
//
//    String dtoJson = objectMapper.writeValueAsString(dto);
//
//    mockMvc.perform(
//            MockMvcRequestBuilders.post("/v1/products")
//                .content(dtoJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("상품 등록 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//
//  @Test
//  @DisplayName("단일 상품 조회 성공")
//  public void testProductGetByProductIdSuccess() throws Exception {
//
//    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
//    mockMvc.perform(
//            MockMvcRequestBuilders.get("/v1/products/{product_id}",
//                    productId)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("단일 상품 조회 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//  @Test
//  @DisplayName("단일 상품 조회 실패")
//  public void testProductGetByProductIdFail() throws Exception {
//
//    UUID productId = UUID.randomUUID();
//    mockMvc.perform(
//            MockMvcRequestBuilders.get("/v1/products/{product_id}",
//                    productId)
//                .header("X-USER-ID", 0L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isBadRequest()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//
//  @Test
//  @DisplayName("상품 목록 조회 성공")
//  public void testProductGetSuccess() throws Exception {
//    mockMvc.perform(
//            MockMvcRequestBuilders.get("/v1/products")
//                .header("X-USER-ID", 0L)
//                .header("X-USER-ROLE", "ROLE_USER")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("상품 목록 조회 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//
//  @Test
//  @DisplayName("상품 수정 성공")
//  public void testProductPutByIdSuccess() throws Exception {
//
//    UUID productId = UUID.randomUUID();
//
//    ReqProductPutByProductIdDtoApiV1 reqDto = ReqProductPutByProductIdDtoApiV1.builder()
//        .product(ReqProductPutByProductIdDtoApiV1.Product.builder()
//            .name("수정된 상품 이름")
//            .price(BigDecimal.valueOf(12500))
//            .image(Image.builder()
//                .imageId(new ArrayList<>())
//                .imageUrl(new ArrayList<>())
//                .build())
//            .build())
//        .build();
//
//    String dtoJson = objectMapper.writeValueAsString(reqDto);
//
//    mockMvc.perform(
//            MockMvcRequestBuilders.put("/v1/products/{productId}", productId)
//                .content(dtoJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("상품 수정 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//  @Test
//  @DisplayName("상품 삭제 성공")
//  public void testProductDeleteSuccess() throws Exception {
//
//    UUID productId = UUID.randomUUID();
//
//    mockMvc.perform(
//            MockMvcRequestBuilders.delete("/v1/products/{productId}", productId)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("상품 삭제 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//  @Test
//  @DisplayName("재고 조회 성공")
//  public void testProductGetStockSucceess() throws Exception {
//
//    UUID productId = UUID.randomUUID();
//
//    mockMvc.perform(
//            MockMvcRequestBuilders.get("/v1/products/{productId}/stock", productId)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("재고 조회 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//
//
//  @Test
//  @DisplayName("재고 수정 성공")
//  public void testProductPatchByIdSuccess() throws Exception {
//
//    UUID productId = UUID.randomUUID();
//
//    ReqProductPatchByProductIdDtoApiV1 reqDto = ReqProductPatchByProductIdDtoApiV1.builder()
//        .product(ReqProductPatchByProductIdDtoApiV1.Product.builder()
//            .stock(50)
//            .build())
//        .build();
//
//    String dtoJson = objectMapper.writeValueAsString(reqDto);
//
//    mockMvc.perform(
//            MockMvcRequestBuilders.patch("/v1/products/{productId}/stock", productId)
//                .content(dtoJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-USER-ID", 1L)
//                .header("X-USER-ROLE", "ROLE_COMPANY")
//        )
//        .andExpectAll(
//            MockMvcResultMatchers.status().isOk(),
//            MockMvcResultMatchers.jsonPath("$.code").value(0),
//            MockMvcResultMatchers.jsonPath("$.message").value("재고 수정 성공"),
//            MockMvcResultMatchers.jsonPath("$.data").doesNotExist()
//        )
//        .andDo(MockMvcResultHandlers.print());
//  }
//}