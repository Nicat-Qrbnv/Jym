package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TraineeMapper {
  private final UserMapper userMapper;

  public TraineeDto toTraineeDto(Trainee source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    return new TraineeDto(
        source.getId(),
        user == null ? null : user.getId(),
        user == null ? null : user.getUsername(),
        source.getDateOfBirth(),
        source.getAddress());
  }

  public CredentialsDto toCredentialsDto(Trainee source) {
    if (source == null) {
      return null;
    }
    return userMapper.getCredentialsDto(source.getUser());
  }
}
