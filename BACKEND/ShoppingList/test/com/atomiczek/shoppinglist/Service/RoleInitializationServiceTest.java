package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializationServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Test
    public void shouldInsertRoleRecordsIfTableIsEmpty(){
        Mockito.when(roleRepository.count()).thenReturn(0L);

        RoleInitializationService roleInitializationService = new RoleInitializationService(roleRepository);

        roleInitializationService.initializeRoles();

        Mockito.verify(roleRepository, Mockito.times(UserRoleEnum.values().length)).save(Mockito.any());


    }


}