package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializationServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Test
    public void shouldInsertRoleRecordsIfTableIsEmpty(){
        when(roleRepository.count()).thenReturn(0L);

        RoleInitializationService roleInitializationService = new RoleInitializationService(roleRepository);
        roleInitializationService.initializeRoles();

        verify(roleRepository, times(UserRoleEnum.values().length)).save(any());
    }
}