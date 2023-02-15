package yeonjeans.saera.security.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserDetailService extends DefaultOAuth2UserService {

  @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
      String email = null;

      System.out.println("userRequst: "+userRequest);

      String clientName = userRequest.getClientRegistration().getClientName();

      System.out.println("clientName: "+clientName);

      System.out.println(userRequest.getAdditionalParameters());
      System.out.println("------------------------------");
      OAuth2User oAuth2User = super.loadUser(userRequest);
      oAuth2User.getAttributes().forEach((k,v)->{
        System.out.println(k+": "+v);
      });

      if(clientName.equals("Google")){
        email = oAuth2User.getAttribute("email");
      }
      System.out.println("email: "+email);


      return oAuth2User;
  }
}
