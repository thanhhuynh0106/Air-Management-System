package com.ams.airManagement.service.implement;

import com.ams.airManagement.dto.LoginRequestDTO;
import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.dto.UsersDTO;
import com.ams.airManagement.entity.Users;
import com.ams.airManagement.exception.OurException;
import com.ams.airManagement.repository.UsersRepository;
import com.ams.airManagement.service.interfac.UserServiceInterface;
import com.ams.airManagement.utils.JWTUtils;
import com.ams.airManagement.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserServiceInterface {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public ResponseDTO register(Users users) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (users.getRole() == null || users.getRole().isBlank()) {
                users.setRole("USER");
            }

            if (usersRepository.existsByEmail(users.getEmail())) {
                throw new OurException(users.getEmail() + " already exists!");
            }

            users.setPassword(passwordEncoder.encode(users.getPassword()));
            Users savedUser = usersRepository.save(users);
            UsersDTO usersDTO = Utils.mapUserEntityToUserDTO(savedUser);
            responseDTO.setStatusCode(200);
            responseDTO.setUsersDTO(usersDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error during user registration: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO login(LoginRequestDTO loginRequest) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = usersRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException("User not found!"));

            var token = jwtUtils.generateToken(user);
            responseDTO.setStatusCode(200);
            responseDTO.setToken(token);
            responseDTO.setRole(user.getRole());
            responseDTO.setExpirationTime("1 day");
            responseDTO.setMessage("Successful!");

        } catch (OurException e){
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error during user login: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getAllUsers() {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<Users> usersList = usersRepository.findAll();
            List<UsersDTO> usersDTOList = Utils.mapUserListEntityToUserListDTO(usersList);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
            responseDTO.setUserList(usersDTOList);

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error getting all users: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserById(Integer userId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Users users = usersRepository.findById(userId).orElseThrow(() -> new OurException("User not found!"));
            UsersDTO usersDTO = Utils.mapUserEntityToUserDTO(users);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
            responseDTO.setUsersDTO(usersDTO);
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error get user by ID: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserByEmail(String email) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Users users = usersRepository.findByEmail(email).orElseThrow(() -> new OurException("User not found!"));
            UsersDTO usersDTO = Utils.mapUserEntityToUserDTO(users);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
            responseDTO.setUsersDTO(usersDTO);
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error get user by Email: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO deleteUser(Integer userId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            usersRepository.findById(userId).orElseThrow(() -> new OurException("User not found!"));
            usersRepository.deleteById(userId);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error deleting user: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO updateUser(Integer userId, UsersDTO updateUserDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Users users = usersRepository.findById(userId).orElseThrow(() -> new OurException("User not found!"));

            if (updateUserDTO.getUsername() != null) {
                users.setUsername(updateUserDTO.getUsername());
            }

            if (updateUserDTO.getEmail() != null) {
                users.setEmail(updateUserDTO.getUsername());
            }

            // if want to update password then add password to userDTO
            // if (updateUserDTO.getPassword() != null) {
            //     user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
            // }

            Users updatedUser = usersRepository.save(users);
            UsersDTO responseUserDTO = Utils.mapUserEntityToUserDTO(updatedUser);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("User updated successfully!");
            responseDTO.setUsersDTO(responseUserDTO);
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error updating user: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getMyInfo(String email) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Users users = usersRepository.findByEmail(email).orElseThrow(() -> new OurException("User not found!"));
            UsersDTO usersDTO = Utils.mapUserEntityToUserDTO(users);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
            responseDTO.setUsersDTO(usersDTO);
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error getting user info: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserBooking(Integer userId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Users users = usersRepository.findById(userId).orElseThrow(() -> new OurException("User not found!"));
            UsersDTO usersDTO = Utils.mapUserEntityToUserDTOAndBooking(users);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
            responseDTO.setUsersDTO(usersDTO);
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error getting user booking: " + e.getMessage());
        }
        return responseDTO;
    }
}
