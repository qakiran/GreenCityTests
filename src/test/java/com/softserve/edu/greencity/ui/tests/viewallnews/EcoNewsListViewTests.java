package com.softserve.edu.greencity.ui.tests.viewallnews;

import com.softserve.edu.greencity.ui.data.User;
import com.softserve.edu.greencity.ui.data.UserRepository;
import com.softserve.edu.greencity.ui.data.econews.NewsDataRepository;
import com.softserve.edu.greencity.ui.pages.econews.EcoNewsPage;
import com.softserve.edu.greencity.ui.pages.econews.ItemComponent;
import com.softserve.edu.greencity.ui.tests.runner.GreenCityTestRunner;
import com.softserve.edu.greencity.ui.tools.jdbc.services.EcoNewsService;
import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EcoNewsListViewTests extends GreenCityTestRunner {
    String cssBackgroundColorProperty;
    String expectedBackgroundColorRGBA;
    String expectedHoveredByMouseBackgroundColorRGBA;
    List<Integer> screenWidth1, screenWidth2, screenWidthWithContent, screenWidthWithoutContent, screenWidthWithoutImages;

    private final String DEFAULT_IMAGE = "https://ita-social-projects.github.io/GreenCityClient/assets/img/icon/econews/default-image-list-view.png";
    private static List<String> testNewsTitles = new ArrayList<>();

    @AfterTest(alwaysRun = true)
    private void deleteNewsIfCreated() {
        if (testNewsTitles.size() > 0) {
            EcoNewsService ecoNewsService = new EcoNewsService();
            testNewsTitles.forEach(ecoNewsService::deleteNewsByTitle);
        }
    }

    @BeforeClass
    public void beforeClass() {
        cssBackgroundColorProperty = "background-color";
        expectedBackgroundColorRGBA = "rgba(5, 107, 51, 1)";
        expectedHoveredByMouseBackgroundColorRGBA = "rgba(19, 170, 87, 1)";
        screenWidthWithContent = Arrays.asList(1440, 1200);
        screenWidthWithoutContent = Arrays.asList(1024, 768, 667);
        screenWidthWithoutImages = Arrays.asList(576, 575);
        screenWidth1 = Arrays.asList(1400, 1024, 768);
        screenWidth2 = Arrays.asList(576, 360);
    }

    @Test(testName = "GC-707")
    @Description("Verify that Content items are displayed as a list in case if 'List view' option is activated")
    public void isDisplayedListContent() {
        logger.info("Starting isDisplayedListContent");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews()
                .switchToListView();

        Assert.assertTrue(ecoNewsPage.getItemsContainer().hasListViewClassActive());
    }


    @Test(testName = "GC-704")
    @Description("Verify that ‘List view’ icon is present on the 'Eco news' page")
    public void isPresentListView() {
        logger.info("Starting isPresentListView");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews();

        softAssert.assertTrue(ecoNewsPage.isDisplayedListView());

        ecoNewsPage.hoverToListView();
        String hoverListViewIconColor = ecoNewsPage.getListViewButtonComponent().getCssValue(cssBackgroundColorProperty);
        softAssert.assertEquals(hoverListViewIconColor, expectedBackgroundColorRGBA);

        ecoNewsPage
                .switchToListView()
                .hoverToGridView();

        String ListViewIconColor = ecoNewsPage.getListViewButtonComponent().getCssValue(cssBackgroundColorProperty);
        softAssert.assertEquals(ListViewIconColor, expectedHoveredByMouseBackgroundColorRGBA);
        softAssert.assertAll();
    }

    @Test(testName = "GC-710")
    @Description("Verify that 6 first Content items are displayed by deafault")
    public void isDisplayedFirstSixContent() {
        logger.info("Starting isDisplayedFirstSixContent");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews()
                .switchToListView();

        Assert.assertTrue(ecoNewsPage.getItemsContainer().getItemsSize() >= 6);
    }

    @Test(testName = "GC-332")
    @Description("Verify that content items are displayed in a list view")
    public void isDisplayedContentItems() {
        logger.info("Starting isDisplayedContentItems");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews()
                .switchToListView();

        String listViewIconColor = ecoNewsPage.getListViewButtonComponent().getCssValue(cssBackgroundColorProperty);

        softAssert.assertEquals(listViewIconColor, expectedBackgroundColorRGBA);
        softAssert.assertTrue(ecoNewsPage.getItemsContainer().getItemsSize() >= 6);
        softAssert.assertAll();
    }

    @Test(testName = "GC-333")
    @Description("Verify that content items have all specified elements.")
    public void isPresentAllItemElements() {
        logger.info("Starting isPresentAllItemElements");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews()
                .switchToListView();

        ItemComponent firstItem = ecoNewsPage.getItemsContainer().chooseNewsByNumber(1);

        softAssert.assertTrue(firstItem.isDisplayedImage());
        softAssert.assertTrue(firstItem.isDisplayedTags());
        softAssert.assertTrue(firstItem.isDisplayedTitle());
        softAssert.assertTrue(firstItem.isDisplayedContent());
        softAssert.assertTrue(firstItem.isDisplayedDateOfCreation());
        softAssert.assertTrue(firstItem.isDisplayedAuthor());
        softAssert.assertTrue(firstItem.isCorrectDateFormat(firstItem.getDateOfCreationText()));

        softAssert.assertAll();
    }

    @Test(testName = "GC-352")
    @Description("Verify that Content items are displayed in chronological order")
    public void isItemsDisplayedChronological() {
        logger.info("isItemsDisplayedChronological");
        logger.info("isItemsDisplayedChronological");
        EcoNewsPage ecoNewsPage = loadApplication()
                .navigateMenuEcoNews()
                .switchToListView();

        Date date1 = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationDateFormat();
        Date date2 = ecoNewsPage.getItemsContainer().chooseNewsByNumber(1).getDateOfCreationDateFormat();

        softAssert.assertTrue((date1.compareTo(date2) == 0) || (date1.compareTo(date2) > 0));

        //JDBC
        EcoNewsService ecoNewsService = new EcoNewsService();
        Date firstNewsDate = ecoNewsService.getLastNewsCreationDateByTitle(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getTitleText());
        Date secondNewsDate = ecoNewsService.getLastNewsCreationDateByTitle(ecoNewsPage.getItemsContainer().chooseNewsByNumber(1).getTitleText());

        softAssert.assertTrue(firstNewsDate.compareTo(secondNewsDate) > 0);

        softAssert.assertAll();
    }

    @Ignore //Runs too long
    @Test(testName = "GC-720")
    @Description("Verify that content items contain all required UI elements according to mock-up.")
    public void isPresentAllContentElements() {
        logger.info("isPresentAllContentElements");
        User user = UserRepository.get().temporary();
        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getOneRowTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getOneRowTitle().getTitle());

        for (Integer integer : screenWidthWithContent) {
            ecoNewsPage.changeWindowWidth(integer);
            logger.info("set width = "+integer);
            logger.info("script width = "+ecoNewsPage.getWindowWidth(integer));
            ecoNewsPage.switchToListView();
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            logger.info("image " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            logger.info("tags " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            logger.info("title " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            logger.info("content " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            logger.info("date " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            logger.info("dateformat " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());
            logger.info("author " + ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());

        }

        for (Integer integer : screenWidthWithoutContent) {
            ecoNewsPage.changeWindowWidth(integer);
            logger.info("set width = "+integer);
            logger.info("script width = "+ecoNewsPage.getWindowWidth(integer));
            ecoNewsPage.switchToListView();
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            logger.info("image " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            logger.info("tags " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            logger.info("title " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            softAssert.assertFalse(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            logger.info("content " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            logger.info("date " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            logger.info("dateformat " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());
            logger.info("author " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());

        }
        for (Integer integer : screenWidthWithoutImages) {
            ecoNewsPage.changeWindowWidth(integer);
            logger.info("set width = "+integer);
            logger.info("script width = "+ecoNewsPage.getWindowWidth(integer));
            ecoNewsPage.switchToListView();
            if(ecoNewsPage.isActiveListView()){
            softAssert.assertFalse(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            logger.info("image " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedImage());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            logger.info("tags " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTags());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            logger.info("title " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedTitle());
            softAssert.assertFalse(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            logger.info("content " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedContent());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            logger.info("date " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedDateOfCreation());
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            logger.info("dateformat " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isCorrectDateFormat(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getDateOfCreationText()));
            softAssert.assertTrue(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());
            logger.info("author " +ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).isDisplayedAuthor());
            }
        }
        ecoNewsPage.maximizeWindow();
        ecoNewsPage.signOut();
        softAssert.assertAll();
    }


    @Test(testName = "GC-725")
    @Description("Verify that displayed image in List view is default image if user didn’t choose own image during news creation.")
    public void isPresentDefaultImage() {
        logger.info("isPresentDefaultImage");
        User user = UserRepository.get().temporary();

        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getOneRowTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getOneRowTitle().getTitle());

        for (Integer integer : screenWidth1) {
            ecoNewsPage.changeWindowWidth(integer);
            ecoNewsPage.switchToListView();
            String src = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getImage().getAttribute("src");
            softAssert.assertEquals(src, DEFAULT_IMAGE);
        }
        for (Integer integer : screenWidth2) {
            ecoNewsPage.changeWindowWidth(integer);
            //On small screen resolution list view automatically switches off
            softAssert.assertFalse(ecoNewsPage.isListViewPresent());
            String src = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getImage().getAttribute("src");
            softAssert.assertEquals(src, DEFAULT_IMAGE);
        }

        ecoNewsPage.maximizeWindow();
        ecoNewsPage.signOut();
        softAssert.assertAll();
    }


    @Test(testName = "GC-708")
    @Description("Verify that when Title consist of 4 row, then Description consist of 0 row.")
    public void isZeroRowDescriptionWhenFourRowsTitle() {
        logger.info("isZeroRowDescriptionWhenFourRowsTitle");
        User user = UserRepository.get().temporary();

        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getFourRowsTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getFourRowsTitle().getTitle());

        ecoNewsPage.changeWindowWidth(1400);

        ecoNewsPage.switchToListView();

        Assert.assertEquals(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getTitleHeight(), 128);
        Assert.assertEquals(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getTitleNumberRow(), 4);
        Assert.assertFalse(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getContent().isDisplayed());
        Assert.assertEquals(ecoNewsPage.getItemsContainer().chooseNewsByNumber(0).getContentNumberVisibleRow(), 0);

        ecoNewsPage.signOut();
        softAssert.assertAll();
    }


    @Test(testName = "GC-723")
    @Description("Verify that when Title consist of 3 row, then Description consist of 1 row.")
    public void isOneRowDescriptionWhenThreeRowsTitle() {
        logger.info("isOneRowDescriptionWhenThreeRowsTitle");
        User user = UserRepository.get().temporary();

        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getThreeRowsTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getThreeRowsTitle().getTitle());

        ecoNewsPage.changeWindowWidth(1400);

        ecoNewsPage.switchToListView();

        ItemComponent firstItemTitle = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0);
        softAssert.assertEquals(firstItemTitle.getTitleHeight(), 96);
        softAssert.assertEquals(firstItemTitle.getTitleNumberRow(), 3);
        softAssert.assertEquals(firstItemTitle.getContentNumberVisibleRow(), 1);

        ecoNewsPage.signOut();
        softAssert.assertAll();
    }


    @Test(testName = "GC-722")
    @Description("Verify that when Title consist of 2 row, then Description consist of 2 row.")
    public void isTwoRowsDescriptionWhenTwoRowsTitle() {
        logger.info("isTwoRowsDescriptionWhenTwoRowsTitle");
        User user = UserRepository.get().temporary();

        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getTwoRowsTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getTwoRowsTitle().getTitle());

        ecoNewsPage.changeWindowWidth(1400);

        ecoNewsPage.switchToListView();

        ItemComponent firstItemTitle = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0);
        softAssert.assertEquals(firstItemTitle.getTitleHeight(), 64);
        softAssert.assertEquals(firstItemTitle.getTitleNumberRow(), 2);
        softAssert.assertEquals(firstItemTitle.getContentNumberVisibleRow(), 2);

        ecoNewsPage.signOut();
        softAssert.assertAll();
    }


    @Test(testName = "GC-724")
    @Description("Verify that when Title consist of 1 row, then Description consist of 3 row.")
    public void isTwoRowsDescriptionWhenOneRowsTitle() {
        logger.info("isTwoRowsDescriptionWhenOneRowsTitle");
        User user = UserRepository.get().temporary();

        EcoNewsPage ecoNewsPage = loadApplication()
                .signIn()
                .getManualLoginComponent()
                .successfullyLogin(user)
                .navigateMenuEcoNews()
                .gotoCreateNewsPage()
                .fillFields(NewsDataRepository.get().getOneRowTitle())
                .publishNews();

        testNewsTitles.add(NewsDataRepository.get().getOneRowTitle().getTitle());

        ecoNewsPage.changeWindowWidth(1440);

        ecoNewsPage.switchToListView();

        ItemComponent firstItemTitle = ecoNewsPage.getItemsContainer().chooseNewsByNumber(0);
        softAssert.assertEquals(firstItemTitle.getTitleHeight(), 32);
        softAssert.assertEquals(firstItemTitle.getTitleNumberRow(), 1);
        softAssert.assertEquals(firstItemTitle.getContentNumberVisibleRow(), 3);

        ecoNewsPage.signOut();
        softAssert.assertAll();
    }
}

