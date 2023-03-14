package yeonjeans.saera.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yeonjeans.saera.Service.StudyServiceImpl;
import yeonjeans.saera.controller.StudyController;
import yeonjeans.saera.domain.entity.example.ReferenceType;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
public class StudyServiceImplTest {
   @Autowired
   private StudyServiceImpl studyService;
   @Autowired
   private StudyController studyController;

   @Test
   public void getIdList() throws InterruptedException {
      //given
      studyService.getIdList(ReferenceType.WORD);

      //when
      ArrayList<Thread> threads = new ArrayList<>();
      for(int i=0; i<5; i++) {
         Thread t = new Thread(new Sample(i));
         t.start();
         threads.add(t);
      }

      for(int i=0; i<threads.size(); i++) {
         Thread t = threads.get(i);
         try {
            t.join();
         }catch(Exception e) {
            System.out.println("exception!!");
         }
      }
      System.out.println("main end.");
   }

   public class Sample implements Runnable {
      int seq;

      public Sample(int seq) {
         this.seq = seq;
      }

      public void run() {
         System.out.println("[ "+ this.seq +" ]" +"thread start");
         try {
            System.out.println(studyService.testSync(ReferenceType.WORD, 6, seq) + "by " + this.seq);
         } catch (Exception e) {
         }
         System.out.println("[ "+ this.seq +" ]" +"thread end");
      }
   }
}
