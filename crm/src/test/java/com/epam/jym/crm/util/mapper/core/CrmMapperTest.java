package com.epam.jym.crm.util.mapper.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class CrmMapperTest {

  @Test
  void mapShouldReturnNullWhenSourceIsNull() {
    CrmMapper crmMapper = new CrmMapper(List.of(new StringToTargetMapper()));

    TargetDto result = crmMapper.map(null, TargetDto.class);

    assertThat(result).isNull();
  }

  @Test
  void mapShouldUseExactMapperWhenRegistered() {
    CrmMapper crmMapper = new CrmMapper(List.of(new StringToTargetMapper()));

    TargetDto result = crmMapper.map("source", TargetDto.class);

    assertThat(result.value()).isEqualTo("exact:source");
  }

  @Test
  void mapShouldUseAssignableSourceMapperWhenExactMapperIsMissing() {
    CrmMapper crmMapper = new CrmMapper(List.of(new NumberToTargetMapper()));

    TargetDto result = crmMapper.map(15, TargetDto.class);

    assertThat(result.value()).isEqualTo("number:15");
  }

  @Test
  void mapCollectionShouldMapEverySourceItem() {
    CrmMapper crmMapper = new CrmMapper(List.of(new StringToTargetMapper()));

    List<TargetDto> result =
        crmMapper.mapCollection(List.of("first", "second"), TargetDto.class).toList();

    assertThat(result)
        .extracting(TargetDto::value)
        .containsExactly("exact:first", "exact:second");
  }

  @Test
  void mapShouldThrowExceptionWhenNoMapperIsRegistered() {
    CrmMapper crmMapper = new CrmMapper(List.of(new StringToTargetMapper()));

    assertThatThrownBy(() -> crmMapper.map(15, TargetDto.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No mapper registered for java.lang.Integer -> " + TargetDto.class.getName());
  }

  private record TargetDto(String value) {}

  private static class StringToTargetMapper implements Mapper<String, TargetDto> {

    @Override
    public Class<String> sourceType() {
      return String.class;
    }

    @Override
    public Class<TargetDto> targetType() {
      return TargetDto.class;
    }

    @Override
    public TargetDto map(String source, MappingContext context) {
      return new TargetDto("exact:" + source);
    }
  }

  private static class NumberToTargetMapper implements Mapper<Number, TargetDto> {

    @Override
    public Class<Number> sourceType() {
      return Number.class;
    }

    @Override
    public Class<TargetDto> targetType() {
      return TargetDto.class;
    }

    @Override
    public TargetDto map(Number source, MappingContext context) {
      return new TargetDto("number:" + source);
    }
  }
}
