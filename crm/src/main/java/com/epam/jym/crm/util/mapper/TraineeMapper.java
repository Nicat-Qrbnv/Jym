package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TraineeMapper {
  private final UserMapper userMapper;
  @Setter(onMethod_ = {@Autowired})
  private TrainerMapper trainerMapper;

  public TraineeProfileDto toTraineeProfileDto(Trainee source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    return new TraineeProfileDto(
        userMapper.toUserDto(user),
        source.getDateOfBirth(),
        source.getAddress(),
        toTrainerDtos(source.getTrainers()));
  }

  public List<TrainerDto> toTrainerDtos(List<Trainer> trainers) {
    if (trainers == null) {
      return List.of();
    }
    return trainers.stream().map(trainerMapper::toTrainerDto).toList();
  }

  public TraineeDto toTraineeDto(Trainee source) {
    User user = source.getUser();
    return new TraineeDto(
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        source.getDateOfBirth(),
        source.getAddress()
    );
  }
}
