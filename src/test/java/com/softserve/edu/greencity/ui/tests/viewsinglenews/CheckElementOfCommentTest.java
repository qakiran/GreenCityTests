package com.softserve.edu.greencity.ui.tests.viewsinglenews;

import com.softserve.edu.greencity.ui.data.User;
import com.softserve.edu.greencity.ui.data.UserRepository;
import com.softserve.edu.greencity.ui.data.econews.NewsDataRepository;
import com.softserve.edu.greencity.ui.pages.common.CommentComponent;
import com.softserve.edu.greencity.ui.pages.common.CommentContainer;
import com.softserve.edu.greencity.ui.pages.common.ReplyContainer;
import com.softserve.edu.greencity.ui.pages.econews.EcoNewsPage;
import com.softserve.edu.greencity.ui.pages.econews.SingleNewsPage;
import com.softserve.edu.greencity.ui.tests.runner.GreenCityTestRunner;
import com.softserve.edu.greencity.ui.tools.jdbc.services.EcoNewsService;
import io.qameta.allure.Description;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class CheckElementOfCommentTest extends GreenCityTestRunner {

    private User getTemporaryUser() {
        return UserRepository.get().temporary();
    }
    private EcoNewsService getEcoNewsService() {
        return new EcoNewsService();
    }

    @BeforeClass
    public void createNews(){

        CommentComponent commentComponent = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getNewsWithValidData())
                .publishNews()
                .switchToSingleNewsPageByNumber(0)
                .getPublishComment()
                .addComment("First Comment")
                .getCommentContainer()
                .chooseCommentByNumber(0)
                .addReply("First reply");
        signOutByStorage();
    }
    @AfterClass
    public void deleteNews() {
        EcoNewsPage ecoNewsPage = loadApplication().navigateMenuEcoNews();
        getEcoNewsService().deleteNewsByTitle(NewsDataRepository.get().getNewsWithValidData().getTitle());
        softAssert.assertFalse(ecoNewsPage.refreshPage().isNewsDisplayedByTitle(NewsDataRepository.get().getNewsWithValidData().getTitle()));
        softAssert.assertAll();
    }

    @Test
    @Description("GC-908")
    public void unloggedUserCannotLikeTheCommentAndReply() {
        logger.info("Verify that unlogged user cannot like the comment/reply on News Single Page starts");

        CommentComponent commentComponent = loadApplication()
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByNumber(0).getCommentContainer()
                .chooseCommentByNumber(0);

        softAssert.assertFalse(commentComponent.isLikesButtonDisplayed());
        softAssert.assertFalse(commentComponent.isReplyButtonDisplayed());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-914")
    public void unloggedUserCanReviewReply() {
        logger.info("Verify that unlogged user cannot like the comment/reply on News Single Page starts");

        boolean isReplyDisplayed = loadApplication()
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByNumber(0).getCommentContainer()
                .chooseCommentByNumber(0)
                .openReply().isReplyComponentPresent();
        softAssert.assertTrue(isReplyDisplayed);
        softAssert.assertAll();
    }

    @Test
    @Description("GC-920")
    public void unloggedUserCanSeeLikes() {
        logger.info("Verify that unlogged user can see the total likes number related to the comments and/or replies");

        CommentComponent commentComponent = loadApplication()
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByNumber(0).getCommentContainer()
                .chooseCommentByNumber(0);
        String commentNumberOfLikes = commentComponent.getLikesNumber();
        softAssert.assertEquals(commentNumberOfLikes, "0");
        String replyNumberOfLikes = commentComponent.openReply().chooseReplyByNumber(0).getReplyLikesNumber();
        softAssert.assertEquals(replyNumberOfLikes, "0");
        softAssert.assertAll();
    }

    @Test
    @Description("GC-922")
    public void repliesToTheCommentAreHidden() {
        logger.info("Verify that replies to the comment are hidden by default for all users(logged and not logged)");

        Boolean isReplyComponentPresentLoginUser = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByNumber(0).getCommentContainer()
                .chooseCommentByNumber(0)
                .isReplyComponentPresent();
        softAssert.assertFalse(isReplyComponentPresentLoginUser);
        signOutByStorage();

        Boolean isReplyComponentPresentNotLoggedUser = loadApplication()
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByNumber(0).getCommentContainer()
                .chooseCommentByNumber(0)
                .isReplyComponentPresent();
        softAssert.assertFalse(isReplyComponentPresentNotLoggedUser);
        softAssert.assertAll();
    }


}
