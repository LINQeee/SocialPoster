package com.telegram.social_poster.Controllers;


import com.telegram.social_poster.Config.GoogleOAuthInitializer;
import com.telegram.social_poster.Services.AuthServices.FacebookAuthService;
import com.telegram.social_poster.Services.AuthServices.InstagramAuthService;
import com.telegram.social_poster.Services.EntityServices.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserEntityService userEntityService;
    private final FacebookAuthService facebookAuthService;
    private final InstagramAuthService instagramAuthService;

    @GetMapping("/google-auth")
    public String handleGoogleAuthCallback(@RequestParam String code, @RequestParam("state") String userId) {
        String refreshToken = GoogleOAuthInitializer.googleAuthService.generateRefreshToken(code);
        userEntityService.changeRefreshToken(Long.valueOf(userId), refreshToken);

        return "successfulAuth/index";
    }

    @GetMapping("/facebook-auth")
    public String handleFacebookAuthCallback(@RequestParam String code, @RequestParam("state") String userId) {
        String accessToken = facebookAuthService.generateAccessToken(code);
        String longLiveAccessToken = facebookAuthService.generateLongLiveAccessToken(accessToken);
        userEntityService.changeFacebookAccessToken(userId, longLiveAccessToken);

        String facebookPageId = facebookAuthService.getFacebookPageId(longLiveAccessToken);
        userEntityService.changeFacebookPageId(userId, facebookPageId);

        String instagramPageId = instagramAuthService.getInstagramPageId(longLiveAccessToken, facebookPageId);
        userEntityService.changeInstagramPageId(userId, instagramPageId);

        String facebookPageAccessToken = facebookAuthService.getFacebookPageAccessToken(longLiveAccessToken, facebookPageId);
        String facebookPageLongLiveAccessToken = facebookAuthService.generateLongLiveAccessToken(facebookPageAccessToken);
        userEntityService.changeFacebookPageAccessToken(userId, facebookPageLongLiveAccessToken);
        return "successfulAuth/index";
    }
}
