package com.epam.jym.crm.facade;

import com.epam.jym.crm.auth.Authenticated;
import com.epam.jym.crm.auth.SkipAuthentication;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
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
      CredentialsDto credentials, Long traineeId, List<Long> trainerIds);

  @SkipAuthentication
  CredentialsDto createTrainer(TrainerCreateDto trainerDto);

  TrainerDto updateTrainer(CredentialsDto credentials, Long trainerId, TrainerUpdateDto trainerDto);

  TrainerDto selectTrainer(CredentialsDto credentials, Long trainerId);

  TrainerDto selectTrainerByUsername(CredentialsDto credentials, String username);

  List<TrainerDto> selectAllTrainers(CredentialsDto credentials);

  List<TrainerDto> selectTrainersNotAssignedToTrainee(
      CredentialsDto credentials, String traineeUsername);

  TrainingDto createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto);

  TrainingDto getTraining(CredentialsDto credentials, Long trainingId);

  List<TrainingDto> getAllTrainings(CredentialsDto credentials);

  List<TrainingDto> getTraineeTrainings(
      CredentialsDto credentials, String traineeUsername, TraineeTrainingsCriteriaDto criteria);

  List<TrainingDto> getTrainerTrainings(
      CredentialsDto credentials, String trainerUsername, TrainerTrainingsCriteriaDto criteria);
}
