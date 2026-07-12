package com.epam.jym.crm.facade;

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
import com.epam.jym.crm.dto.user.CreatedCredentialsDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import java.util.List;

public interface CrmFacade {

  void login(CredentialsDto credentials);

  void changeLogin(String username, PasswordUpdateDto passwordUpdateDto);

  CreatedCredentialsDto createTrainee(TraineeCreateDto traineeDto);

  UpdatedTraineeProfileDto updateTraineeProfile(String username, TraineeUpdateDto traineeDto);

  void changeUserStatus(String username, boolean isActive);

  void deleteTrainee(String username);

  TraineeProfileDto getTraineeProfile(String username);

  List<TrainerSummaryDto> updateTraineeTrainers(
      String traineeUsername, List<String> trainerUsernames);

  CreatedCredentialsDto createTrainer(TrainerCreateDto trainerDto);

  UpdatedTrainerProfileDto updateTrainerProfile(String username, TrainerUpdateDto trainerDto);

  TrainerProfileDto getTrainerProfile(String username);

  List<TrainerSummaryDto> getNotAssignedActiveTrainers(String traineeUsername);

  List<TraineeTrainingDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingsCriteriaDto criteria);

  List<TrainerTrainingDto> getTrainerTrainings(
      String trainerUsername, TrainerTrainingsCriteriaDto criteria);

  List<TrainingTypeDto> getTrainingTypes();

  void createTraining(TrainingCreateDto trainingDto);
}
