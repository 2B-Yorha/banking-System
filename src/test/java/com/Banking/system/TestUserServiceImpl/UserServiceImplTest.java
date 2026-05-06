package com.Banking.system.TestUserServiceImpl;

import com.Banking.system.dto.request.UpdateUserRequest;
import com.Banking.system.dto.response.UserResponse;
import com.Banking.system.entity.User;
import com.Banking.system.enums.Role;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.Service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;



    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .firstName("Jotaro")
                .lastName("Kujo")
                .email("jstarplatinum@gmail.com")
                .phone("1234567890")
                .role(Role.USER)
                .enabled(true)
                .build();
    }



    //Get My Profile Test:
    @Test
    void getMyProfile_Success() {
        when(userRepository.findByEmail("jstarplatinum@gmail.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getMyProfile("jstarplatinum@gmail.com");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("jstarplatinum@gmail.com");
        assertThat(response.getFirstName()).isEqualTo("Jotaro");
    }

    @Test
    void getMyProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyProfile("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }




    //Updating My Profile

    @Test
    void updateMyProfile_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Josepth");
        request.setLastName("Joestar");
        request.setPhone("0987654321");

        when(userRepository.findByEmail("jstarplatinum@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateMyProfile("jstarplatinum@gmail.com", request);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateMyProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateMyProfile("unknown@example.com", new UpdateUserRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }




    //Admiin tests:
    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("jstarplatinum@gmail.com");
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toggleUserStatus_DisablesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserStatus(1L);

        verify(userRepository).save(argThat(u -> !u.isEnabled()));
    }

    @Test
    void toggleUserStatus_EnablesDisabledUser() {
        user.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserStatus(1L);

        verify(userRepository).save(argThat(User::isEnabled));
    }


}
