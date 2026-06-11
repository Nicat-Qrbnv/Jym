package com.epam.jym.crm.facade;

import com.epam.jym.crm.aspect.auth.Authenticated;
import com.epam.jym.crm.aspect.auth.SkipAuthentication;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainee.UpdatedTraineeProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerSummaryDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.trainer.UpdatedTrainerProfileDto;
import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import java.util.List;

@Authenticated
public interface CrmFacade {

  void login(CredentialsDto credentials);

  void changeLogin(CredentialsDto credentials, PasswordUpdateDto passwordUpdateDto);

  @SkipAuthentication
  CredentialsDto createTrainee(TraineeCreateDto traineeDto);

  UpdatedTraineeProfileDto updateTraineeProfile(
      CredentialsDto credentials, String username, TraineeUpdateDto traineeDto);

  void changeUserStatus(CredentialsDto credentials, String username, boolean isActive);

  void deleteTrainee(CredentialsDto credentials, String username);

  TraineeProfileDto getTraineeProfile(CredentialsDto credentials, String username);

  List<TrainerSummaryDto> updateTraineeTrainers(
      CredentialsDto credentials, String traineeUsername, List<String> trainerUsernames);

  @SkipAuthentication
  CredentialsDto createTrainer(TrainerCreateDto trainerDto);

  UpdatedTrainerProfileDto updateTrainerProfile(
      CredentialsDto credentials, String username, TrainerUpdateDto trainerDto);

  TrainerProfileDto getTrainerProfile(CredentialsDto credentials, String username);

  List<TrainerSummaryDto> getNotAssignedActiveTrainers(
      CredentialsDto credentials, String traineeUsername);

  List<TraineeTrainingDto> getTraineeTrainings(
      CredentialsDto credentials, String traineeUsername, TraineeTrainingsCriteriaDto criteria);

  List<TrainerTrainingDto> getTrainerTrainings(
      CredentialsDto credentials, String trainerUsername, TrainerTrainingsCriteriaDto criteria);

  List<TrainingTypeDto> getTrainingTypes();

  void createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto);
}
