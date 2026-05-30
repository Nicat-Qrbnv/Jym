package com.epam.jym.crm.facade;

import com.epam.jym.crm.auth.Authenticated;
import com.epam.jym.crm.auth.SkipAuthentication;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import java.util.List;

@Authenticated
public interface CrmFacade {

  void login(CredentialsDto credentials);

  void changeLogin(CredentialsDto credentials, String newPassword);

  @SkipAuthentication
  CredentialsDto createTrainee(TraineeCreateDto traineeDto);

  TraineeProfileDto updateTraineeProfile(
      CredentialsDto credentials, String username, TraineeUpdateDto traineeDto);

  void changeUserStatus(CredentialsDto credentials, String username);

  void deleteTrainee(CredentialsDto credentials, String username);

  TraineeProfileDto getTraineeProfile(CredentialsDto credentials, String username);

  List<TrainerDto> updateTraineeTrainers(
      CredentialsDto credentials, String traineeUsername, List<String> trainerUsernames);

  @SkipAuthentication
  CredentialsDto createTrainer(TrainerCreateDto trainerDto);

  TrainerProfileDto updateTrainerProfile(
      CredentialsDto credentials, String username, TrainerUpdateDto trainerDto);

  TrainerProfileDto getTrainerProfile(CredentialsDto credentials, String username);

  List<TrainerDto> getNotAssignedActiveTrainers(CredentialsDto credentials, String traineeUsername);

  List<TrainingDto> getTraineeTrainings(
      CredentialsDto credentials, String traineeUsername, TraineeTrainingsCriteriaDto criteria);

  List<TrainingDto> getTrainerTrainings(
      CredentialsDto credentials, String trainerUsername, TrainerTrainingsCriteriaDto criteria);

  List<TrainingTypeDto> getTrainingTypes();

  void createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto);
}
