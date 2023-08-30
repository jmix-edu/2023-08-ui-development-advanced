package com.company.jmixpm.screen.userinfo;

import com.company.jmixpm.app.PostService;
import com.company.jmixpm.entity.UserInfo;
import com.google.common.collect.ImmutableMap;
import io.jmix.core.LoadContext;
import io.jmix.core.common.util.Preconditions;
import io.jmix.ui.action.Action;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlIdSerializer;
import io.jmix.ui.navigation.UrlParamsChangedEvent;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route("user-info")
@UiController("UserInfoScreen")
@UiDescriptor("user-info-screen.xml")
public class UserInfoScreen extends Screen {

    @Autowired
    private UrlRouting urlRouting;
    @Autowired
    private PostService postService;

    private Long userId;

    public UserInfoScreen withUserId(Long userId) {
        Preconditions.checkNotNullArgument(userId);
        this.userId = userId;
        return this;
    }

    @Install(to = "userInfoDl", target = Target.DATA_LOADER)
    private UserInfo userInfoDlLoadDelegate(final LoadContext<UserInfo> loadContext) {
        UserInfo userInfo = postService.fetchUserInfo(userId);
        if (userInfo == null) {
            throw new IllegalStateException("User wasn't loaded for Id: " + userId);
        }

        return userInfo;
    }

    @Subscribe
    public void onAfterShow(final AfterShowEvent event) {
        String serializedId = UrlIdSerializer.serializeId(userId);
        urlRouting.replaceState(this, ImmutableMap.of("id", serializedId));
    }

    @Subscribe
    public void onUrlParamsChanged(final UrlParamsChangedEvent event) {
        String serializedId = event.getParams().get("id");
        userId = ((Long) UrlIdSerializer.deserializeId(Long.class, serializedId));
    }

    @Subscribe("windowClose")
    public void onWindowClose(final Action.ActionPerformedEvent event) {
        closeWithDefaultAction();
    }
}