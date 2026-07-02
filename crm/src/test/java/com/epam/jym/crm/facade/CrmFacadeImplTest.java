package com.epam.jym.crm.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CreatedCredentialsDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.JwtService;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
import com.epam.jym.crm.util.mapper.core.CrmMapper;
import java.time.LocalDate;
import java.util.LinkedHashSet;
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
  @Mock private AuthenticationService authenticationService;
  @Mock private JwtService jwtService;
  @Mock private UserService userService;
  @Mock private CrmMapper crmMapper;

  @InjectMocks private CrmFacadeImpl crmFacade;

  @Test
  void createTraineeShouldReturnPreparedCredentialsFromCreatedUser() {
    TraineeCreateDto createDto =
        new TraineeCreateDto(new UserCreateDto("John", "Doe"), LocalDate.of(1990, 1, 1), null);
    User user = createUser("john.doe", "generated-password", "John");
    Trainee trainee = createTrainee(user);

    when(traineeService.createTrainee(createDto)).thenReturn(trainee);
    when(crmMapper.map(user, CredentialsDto.class)).thenReturn(CREDENTIALS);
    when(jwtService.generateToken("john.doe")).thenReturn("token");
    when(jwtService.expirationSeconds()).thenReturn(3600L);

    CreatedCredentialsDto result = crmFacade.createTrainee(createDto);

    assertThat(result.credentials()).isSameAs(CREDENTIALS);
    assertThat(result.tokenDetails().token()).isEqualTo("token");
    assertThat(result.tokenDetails().expiresIn()).isEqualTo(3600L);
    verify(crmMapper).map(user, CredentialsDto.class);
  }

  @Test
  void changeLoginShouldDelegatePasswordChangeByCredentialUsername() {
    PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto("old-password", "new-password");

    crmFacade.changeLogin(CREDENTIALS.username(), passwordUpdateDto);

    verify(userService).changePassword("john.doe", passwordUpdateDto);
  }

  @Test
  void loginShouldDelegateToAuthenticationServiceOnly() {
    crmFacade.login(CREDENTIALS);

    verify(authenticationService).authenticate(CREDENTIALS);
    verifyNoInteractions(
        traineeService,
        trainerService,
        trainingService,
        trainingTypeService,
        userService,
        crmMapper);
  }

  @Test
  void updateTraineeProfileShouldDelegateByUsernameAndMapUpdatedTrainee() {
    var userDto = new UserDto("John", "Doe", true);
    var updateDto = new TraineeUpdateDto(userDto, LocalDate.of(1990, 1, 1), "Main st");
    var trainee = createTrainee(createUser("john.doe", "password", "John"));

    var userProfileDto = new UserProfileDto("John.Doe", "John", "Doe");
    var profileDto =
        new UpdatedTraineeProfileDto(userProfileDto, updateDto.dateOfBirth(), "Main st", List.of());

    when(traineeService.updateTraineeProfile("john.doe", updateDto)).thenReturn(trainee);
    when(crmMapper.map(trainee, UpdatedTraineeProfileDto.class)).thenReturn(profileDto);

    UpdatedTraineeProfileDto result =
        crmFacade.updateTraineeProfile("john.doe", updateDto);

    assertThat(result).isSameAs(profileDto);
  }

  @Test
  void changeUserStatusShouldDelegateByUsername() {
    crmFacade.changeUserStatus("john.doe", false);

    verify(userService).changeUserStatus("john.doe", false);
  }

  @Test
  void deleteTraineeShouldDelegateByUsername() {
    crmFacade.deleteTrainee("john.doe");

    verify(traineeService).deleteTrainee("john.doe");
  }

  @Test
  void getTraineeProfileShouldGetTraineeByUsernameAndMapProfile() {
    Trainee trainee = createTrainee(createUser("john.doe", "password", "John"));
    TraineeProfileDto profileDto =
        new TraineeProfileDto(new UserDto("John", "Doe", true), null, null, List.of());

    when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
    when(crmMapper.map(trainee, TraineeProfileDto.class)).thenReturn(profileDto);

    TraineeProfileDto result = crmFacade.getTraineeProfile("john.doe");

    assertThat(result).isSameAs(profileDto);
  }

  @Test
  void updateTraineeTrainersShouldDelegateAndMapUpdatedTrainers() {
    Trainer firstTrainer = createTrainer(createUser("jane.doe", "password", "Jane"));
    Trainer secondTrainer = createTrainer(createUser("kate.doe", "password", "Kate"));
    TrainerSummaryDto firstDto = createTrainerSummaryDto("jane.doe", "Jane");
    TrainerSummaryDto secondDto = createTrainerSummaryDto("kate.doe", "Kate");
    List<String> trainerUsernames = List.of("jane.doe", "kate.doe");
    var trainers = new LinkedHashSet<>(List.of(firstTrainer, secondTrainer));

    when(traineeService.updateTraineeTrainers("john.doe", trainerUsernames)).thenReturn(trainers);
    when(crmMapper.mapCollection(trainers, TrainerSummaryDto.class))
        .thenReturn(Stream.of(firstDto, secondDto));

    List<TrainerSummaryDto> result =
        crmFacade.updateTraineeTrainers("john.doe", trainerUsernames);

    assertThat(result).containsExactly(firstDto, secondDto);
  }

  @Test
  void createTrainerShouldReturnPreparedCredentialsFromCreatedUser() {
    TrainingTypeDto specialization = new TrainingTypeDto(1L, "Fitness");
    TrainerCreateDto createDto =
        new TrainerCreateDto(new UserCreateDto("Jane", "Doe"), specialization);
    User user = createUser("jane.doe", "generated-password", "Jane");
    Trainer trainer = createTrainer(user);

    when(trainerService.createTrainer(createDto)).thenReturn(trainer);
    when(crmMapper.map(user, CredentialsDto.class)).thenReturn(CREDENTIALS);
    when(jwtService.generateToken("john.doe")).thenReturn("token");
    when(jwtService.expirationSeconds()).thenReturn(3600L);

    CreatedCredentialsDto result = crmFacade.createTrainer(createDto);

    assertThat(result.credentials()).isSameAs(CREDENTIALS);
    assertThat(result.tokenDetails().token()).isEqualTo("token");
    assertThat(result.tokenDetails().expiresIn()).isEqualTo(3600L);
    verify(crmMapper).map(user, CredentialsDto.class);
  }

  @Test
  void updateTrainerProfileShouldDelegateByUsernameAndMapUpdatedTrainer() {
    TrainingTypeDto specialization = new TrainingTypeDto(1L, "Fitness");
    TrainerUpdateDto updateDto =
        new TrainerUpdateDto(new UserDto("Jane", "Doe", true), specialization);
    Trainer trainer = createTrainer(createUser("jane.doe", "password", "Jane"));
    UpdatedTrainerProfileDto profileDto =
        new UpdatedTrainerProfileDto(
            new UserProfileDto("jane.doe", "Jane", "Doe"), specialization, List.of());

    when(trainerService.updateTrainerProfile("jane.doe", updateDto)).thenReturn(trainer);
    when(crmMapper.map(trainer, UpdatedTrainerProfileDto.class)).thenReturn(profileDto);

    UpdatedTrainerProfileDto result =
        crmFacade.updateTrainerProfile("jane.doe", updateDto);

    assertThat(result).isSameAs(profileDto);
  }

  @Test
  void getTrainerProfileShouldGetTrainerByUsernameAndMapProfile() {
    TrainingTypeDto specialization = new TrainingTypeDto(1L, "Fitness");
    Trainer trainer = createTrainer(createUser("jane.doe", "password", "Jane"));
    TrainerProfileDto profileDto =
        new TrainerProfileDto(new UserDto("Jane", "Doe", true), specialization, List.of());

    when(trainerService.getTrainerByUsername("jane.doe")).thenReturn(trainer);
    when(crmMapper.map(trainer, TrainerProfileDto.class)).thenReturn(profileDto);

    TrainerProfileDto result = crmFacade.getTrainerProfile("jane.doe");

    assertThat(result).isSameAs(profileDto);
  }

  @Test
  void getNotAssignedActiveTrainersShouldMapServiceResults() {
    Trainer firstTrainer = createTrainer(createUser("jane.doe", "password", "Jane"));
    Trainer secondTrainer = createTrainer(createUser("kate.doe", "password", "Kate"));
    TrainerSummaryDto firstDto = createTrainerSummaryDto("jane.doe", "Jane");
    TrainerSummaryDto secondDto = createTrainerSummaryDto("kate.doe", "Kate");
    List<Trainer> trainers = List.of(firstTrainer, secondTrainer);

    when(trainerService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(trainers);
    when(crmMapper.mapCollection(trainers, TrainerSummaryDto.class))
        .thenReturn(Stream.of(firstDto, secondDto));

    List<TrainerSummaryDto> result =
        crmFacade.getNotAssignedActiveTrainers("john.doe");

    assertThat(result).containsExactly(firstDto, secondDto);
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
        crmFacade.getTraineeTrainings("john.doe", criteria);

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
        crmFacade.getTrainerTrainings("jane.doe", criteria);

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

  @Test
  void createTrainingShouldDelegateToTrainingService() {
    TrainingCreateDto createDto =
        new TrainingCreateDto(
            "Core Basics", "john.doe", "jane.doe", LocalDate.of(2026, 5, 8), 60);
    User traineeUser = createUser("john.doe", "password", "John");
    User trainerUser = createUser("jane.doe", "password", "Jane");
    Training training = createTraining(traineeUser, trainerUser);

    when(trainingService.createTraining(createDto)).thenReturn(training);

    crmFacade.createTraining(createDto);

    verify(trainingService).createTraining(createDto);
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

  private TrainerSummaryDto createTrainerSummaryDto(String username, String firstName) {
    return new TrainerSummaryDto(
        new UserProfileDto(username, firstName, "Doe"), new TrainingTypeDto(1L, "Fitness"));
  }
}
