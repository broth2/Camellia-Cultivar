package com.camellia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.camellia.models.users.User;
import com.camellia.repositories.QuizRepository;
import com.camellia.services.QuizService;



@ExtendWith(MockitoExtension.class)
public class TestUserReputation {

    @Mock
    private User usr;

    @Mock( lenient = true)
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    public void setUp(){
        
        when(usr.getReputation()).thenReturn(0.0);
        when(quizRepository.getUserCorrectAnswersCount(any())).thenReturn(0l);
        when(quizRepository.getUserAnswersCount(any())).thenReturn(0l);
    }

    @Test
    public void reputationScalesProperly(){
        quizService.calculateNewReputation(usr, 6, 0);
        quizService.calculateNewReputation(usr, 6, 1);
        quizService.calculateNewReputation(usr, 6, 2);
        quizService.calculateNewReputation(usr, 6, 3);
        quizService.calculateNewReputation(usr, 6, 4);
        quizService.calculateNewReputation(usr, 6, 5);
        quizService.calculateNewReputation(usr, 6, 6);

        assertEquals(0.0, usr.getReputation());
    }
    
}
