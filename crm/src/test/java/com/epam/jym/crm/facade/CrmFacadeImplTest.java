package com.epam.jym.crm.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
import com.epam.jym.crm.util.mapper.core.CrmMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrmFacadeImplTest {

  private static final CredentialsDto CREDENTIALS = new CredentialsDto("john.doe", "password");

  @Mock private TraineeService traineeService;
  @Mock private TrainerService trainerService;
  @Mock private TrainingService trainingService;
  @Mock private TrainingTypeService trainingTypeService;
  @Mock private UserService userService;
  @Mock private CrmMapper crmMapper;

  @InjectMocks private CrmFacadeImpl crmFacade;

  @Test
  void createTraineeShouldReturnCredentialsFromCreatedUser() {
    TraineeCreateDto createDto =
        new TraineeCreateDto(new UserCreateDto("John", "Doe"), LocalDate.of(1990, 1, 1), null);
    User user = createUser("john.doe", "generated-password", "John");
    Trainee trainee = createTrainee(user);

    when(traineeService.createTrainee(createDto)).thenReturn(trainee);
    when(crmMapper.map(user, CredentialsDto.class)).thenReturn(CREDENTIALS);

    CredentialsDto result = crmFacade.createTrainee(createDto);

    assertThat(result).isSameAs(CREDENTIALS);
    verify(crmMapper).map(user, CredentialsDto.class);
  }

  @Test
  void changeLoginShouldDelegatePasswordChangeByCredentialUsername() {
    crmFacade.changeLogin(CREDENTIALS, "new-password");

    verify(userService).changePassword("john.doe", "new-password");
  }

  @Test
  void updateTraineeProfileShouldDelegateByUsernameAndMapUpdatedTrainee() {
    UserDto userDto = new UserDto("John", "Doe", true);
    TraineeUpdateDto updateDto = new TraineeUpdateDto(userDto, LocalDate.of(1990, 1, 1), "Main st");
    Trainee trainee = createTrainee(createUser("john.doe", "password", "John"));
    TraineeProfileDto profileDto =
        new TraineeProfileDto(userDto, updateDto.dateOfBirth(), "Main st", List.of());

    when(traineeService.updateTraineeProfile("john.doe", updateDto)).thenReturn(trainee);
    when(crmMapper.map(trainee, TraineeProfileDto.class)).thenReturn(profileDto);

    TraineeProfileDto result = crmFacade.updateTraineeProfile(CREDENTIALS, "john.doe", updateDto);

    assertThat(result).isSameAs(profileDto);
  }

  @Test
  void getTraineeTrainingsShouldMapTrainingResults() {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(null, null, "Jane Doe", "Fitness");
    User trainerUser = createUser("jane.doe", "password", "Jane");
    User traineeUser = createUser("john.doe", "password", "John");
    Training training = createTraining(traineeUser, trainerUser);
    TrainingDto trainingDto =
        new TrainingDto("Core Basics", LocalDate.of(2026, 5, 8), "Fitness", 60);
    TraineeTrainingDto traineeTrainingDto = new TraineeTrainingDto("Jane Doe", trainingDto);

    when(trainingService.getTraineeTrainings("john.doe", criteria)).thenReturn(List.of(training));
    when(crmMapper.mapCollection(List.of(training), TraineeTrainingDto.class))
        .thenReturn(Stream.of(traineeTrainingDto));

    List<TraineeTrainingDto> result =
        crmFacade.getTraineeTrainings(CREDENTIALS, "john.doe", criteria);

    assertThat(result).containsExactly(traineeTrainingDto);
    verify(crmMapper).mapCollection(List.of(training), TraineeTrainingDto.class);
  }

  @Test
  void getTrainerTrainingsShouldMapTrainingResults() {
    TrainerTrainingsCriteriaDto criteria = new TrainerTrainingsCriteriaDto(null, null, "John Doe");
    User traineeUser = createUser("john.doe", "password", "John");
    User trainerUser = createUser("jane.doe", "password", "Jane");
    Training training = createTraining(traineeUser, trainerUser);
    TrainingDto trainingDto =
        new TrainingDto("Core Basics", LocalDate.of(2026, 5, 8), "Fitness", 60);
    TrainerTrainingDto trainerTrainingDto = new TrainerTrainingDto("John Doe", trainingDto);

    when(trainingService.getTrainerTrainings("jane.doe", criteria)).thenReturn(List.of(training));
    when(crmMapper.mapCollection(List.of(training), TrainerTrainingDto.class))
        .thenReturn(Stream.of(trainerTrainingDto));

    List<TrainerTrainingDto> result =
        crmFacade.getTrainerTrainings(CREDENTIALS, "jane.doe", criteria);

    assertThat(result).containsExactly(trainerTrainingDto);
    verify(crmMapper).mapCollection(List.of(training), TrainerTrainingDto.class);
  }

  @Test
  void getTrainingTypesShouldMapAllServiceResults() {
    TrainingType fitness = createTrainingType(1L, "Fitness");
    TrainingType yoga = createTrainingType(2L, "Yoga");
    TrainingTypeDto fitnessDto = new TrainingTypeDto(1L, "Fitness");
    TrainingTypeDto yogaDto = new TrainingTypeDto(2L, "Yoga");

    when(trainingTypeService.getAllTypes()).thenReturn(List.of(fitness, yoga));
    when(crmMapper.mapCollection(List.of(fitness, yoga), TrainingTypeDto.class))
        .thenReturn(Stream.of(fitnessDto, yogaDto));

    List<TrainingTypeDto> result = crmFacade.getTrainingTypes();

    assertThat(result).containsExactly(fitnessDto, yogaDto);
  }

  private Training createTraining(User traineeUser, User trainerUser) {
    Training training = new Training();
    training.setName("Core Basics");
    training.setScheduledDate(LocalDate.of(2026, 5, 8));
    training.setDurationInMinutes(60);
    training.setType(createTrainingType(1L, "Fitness"));
    training.setTrainee(createTrainee(traineeUser));
    training.setTrainer(createTrainer(trainerUser));
    return training;
  }

  private Trainee createTrainee(User user) {
    Trainee trainee = new Trainee();
    trainee.setUser(user);
    return trainee;
  }

  private Trainer createTrainer(User user) {
    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(createTrainingType(1L, "Fitness"));
    return trainer;
  }

  private User createUser(String username, String password, String firstName) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    user.setFirstName(firstName);
    user.setLastName("Doe");
    user.setActive(true);
    return user;
  }

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType type = new TrainingType();
    type.setId(id);
    type.setName(name);
    return type;
  }
}
