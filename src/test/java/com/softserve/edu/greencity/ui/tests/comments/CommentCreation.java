package com.softserve.edu.greencity.ui.tests.comments;

import com.softserve.edu.greencity.ui.data.User;
import com.softserve.edu.greencity.ui.data.UserRepository;
import com.softserve.edu.greencity.ui.data.econews.NewsData;
import com.softserve.edu.greencity.ui.data.econews.NewsDataRepository;
import com.softserve.edu.greencity.ui.pages.common.CommentComponent;
import com.softserve.edu.greencity.ui.pages.common.CommentPart;
import com.softserve.edu.greencity.ui.pages.common.ReplyComponent;
import com.softserve.edu.greencity.ui.pages.econews.EcoNewsPage;
import com.softserve.edu.greencity.ui.tests.runner.GreenCityTestRunner;
import com.softserve.edu.greencity.ui.tools.jdbc.services.EcoNewsService;
import io.qameta.allure.Description;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CommentCreation extends GreenCityTestRunner {
    private NewsData news;

    private User getTemporaryUser() {
        return UserRepository.get().temporary();
    }

    private EcoNewsService getEcoNewsService() {
        return new EcoNewsService();
    }

    @BeforeClass
    public void createNews() {
        news = NewsDataRepository.get().getNewsWithValidData("Comment Creation test");
        CommentComponent commentComponent = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(news)
                .publishNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .addComment("First Comment")
                .chooseCommentByNumber(0)
                .addReply("First reply");
        signOutByStorage();
    }

    @AfterClass
    public void deleteNews() {
        EcoNewsPage ecoNewsPage = loadApplication().navigateMenuEcoNews();
        getEcoNewsService().deleteNewsByTitle(news.getTitle());
        softAssert.assertFalse(ecoNewsPage.refreshPage().isNewsDisplayedByTitle(news.getTitle()));
    }

    @Test
    @Description("GC-958")
    public void loggedUserCanPublishReply() {
        logger.info("Verify that logged user can publish reply starts");

        String replyText = "Test reply";
        ReplyComponent replyComponent = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .chooseCommentByNumber(0)
                .addReply(replyText).openReply().chooseReplyByNumber(0);

        softAssert.assertEquals(replyText, replyComponent.getReplyComment().getText());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-817")
    public void loggedUserCanAddComment() {
        logger.info("Verify that logged user can add comment starts");
        String commentText = "Test comment";
        CommentPart commentPart = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .addComment(commentText);

        softAssert.assertEquals(commentText, commentPart.chooseCommentByNumber(0).getCommentText());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-821")
    public void loggedUserCannotAddEmptyComment() {
        logger.info("Verify that logged user cannot add empty comment starts");
        String commentText = "";
        CommentPart commentPart = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .setCommentText(commentText);

        softAssert.assertFalse(commentPart.isPublishCommentButtonEnable());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-826")
    public void unloggedUserCannotDeleteCommentAndReply() {
        logger.info("Verify that unregistered user can’t delete comment/reply starts");

        CommentComponent createTestData = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .addComment("Check Delete")
                .chooseCommentByNumber(0)
                .addReply("Check Delete");
        signOutByStorage();
        CommentComponent commentComponent = loadApplication()
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .chooseCommentByNumber(0);

        softAssert.assertFalse(commentComponent.isDeleteCommentButtonDisplayed());
        softAssert.assertFalse(commentComponent.openReply().chooseReplyByNumber(0).isDeleteReplyButtonDisplayed());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-819")
    public void loggedUserCanDeleteHisComment() {
        logger.info("Verify that logged user can add comment starts");
        String commentText = "Delete comment";
        CommentComponent commentComponent = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart()
                .addComment(commentText)
                .chooseCommentByNumber(0)
                .clickDeleteCommentButton();
        softAssert.assertFalse(commentComponent.isCommentPresent());
        softAssert.assertAll();
    }

    @Test
    @Description("GC-827")
    public void systemRecalculatesTheTotalCommentsNumber() {
        logger.info("Verify that after comment was published, system recalculates the total com.softserve.edu.greencity.api.comments number starts");
        String commentText = "Test comment";
        CommentPart commentPart = loadApplication()
                .loginIn(getTemporaryUser())
                .navigateMenuEcoNews()
                .switchToSingleNewsPageByParameters(news)
                .getCommentPart();
        int numberBeforePublish = commentPart.getNumberOfComment();
        int numberAfterPublish = commentPart.addComment(commentText).getNumberOfComment();

        softAssert.assertEquals(numberBeforePublish + 1, numberAfterPublish);
        softAssert.assertAll();
    }

}
