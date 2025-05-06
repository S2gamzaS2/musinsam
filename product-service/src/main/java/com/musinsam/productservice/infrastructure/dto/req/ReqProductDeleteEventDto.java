package com.musinsam.productservice.infrastructure.dto.req;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqProductDeleteEventDto {

  List<UUID> productIdList;

}
