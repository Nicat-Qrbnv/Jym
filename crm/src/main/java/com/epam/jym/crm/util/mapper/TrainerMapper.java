package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainerMapper {
  private final UserMapper userMapper;

  public TrainerDto toTrainerDto(Trainer source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    TrainingType specialization = source.getSpecialization();
    return new TrainerDto(
        source.getId(),
        user == null ? null : user.getId(),
        user == null ? null : user.getUsername(),
        specialization == null
            ? null
            : new TrainingTypeDto(specialization.getId(), specialization.getName()));
  }

  public List<TrainerDto> toTrainerDtoList(List<Trainer> source) {
    if (source == null || source.isEmpty()) {
      return null;
    }
    return source.stream().map(this::toTrainerDto).toList();
  }

  public CredentialsDto toCredentialsDto(Trainer source) {
    if (source == null) {
      return null;
    }
    return userMapper.getCredentialsDto(source.getUser());
  }
}
