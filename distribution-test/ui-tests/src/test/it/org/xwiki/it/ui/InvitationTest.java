/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.it.ui;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import javax.mail.internet.MimeMessage;
import javax.mail.Multipart;
import javax.mail.Address;
import javax.mail.BodyPart;

import com.icegreen.greenmail.util.GreenMail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import org.xwiki.it.ui.elements.InvitationSenderPage;
import org.xwiki.it.ui.elements.InvitationMessageDisplayElement;
import org.xwiki.it.ui.elements.AdminSectionPage;
import org.xwiki.it.ui.elements.TableElement;
import org.xwiki.it.ui.elements.InspectInvitationsPage;
import org.xwiki.it.ui.elements.InvitationGuestActionsPage;
import org.xwiki.it.ui.elements.RegisterPage;
import org.xwiki.it.ui.elements.EditObjectsPage;
import org.xwiki.it.ui.elements.FormElement;
import org.xwiki.it.ui.elements.InvitationActionConfirmationElement;

import org.xwiki.it.ui.framework.AbstractTest;
import org.xwiki.it.ui.framework.TestUtils;

/**
 * Tests invitation application.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class InvitationTest extends AbstractTest
{
    private static boolean initialized;

    private InvitationSenderPage senderPage;

    private GreenMail greenMail;

    @Before
    public void setUp()
    {
        // Login as admin and delete existing messages.
        getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(
                            getUtil().getURLToDeletePage("Invitation", "InvitationMessages")));

        senderPage = newSenderPage();

        if (!initialized) {
            // We have to go to sender page before any config shows up.
            senderPage.gotoPage();
            // Set port to 3025
            AdminSectionPage config = new AdminSectionPage("Invitation");
            config.gotoPage();
            config.getForm().setFieldValue(By.id("Invitation.InvitationConfig_Invitation.WebHome_0_smtp_port"), 
                                           "3025");
            config.clickSave();

            // Setup language pack
            config = new AdminSectionPage("Programming");
            config.gotoPage();
            config.getForm().setFieldValue(By.id("XWiki.XWikiPreferences_0_documentBundles"), 
                                           "Invitation.InvitationDocumentBundle");
            config.clickSave();
            initialized = true;
        }

        senderPage.gotoPage();
        senderPage.fillInDefaultValues();
    }

    @Test
    public void testGuestActionsOnNonexistantMessage() throws Exception
    {
        TestUtils.Session s = getUtil().getSession();
        try {
            getUtil().setSession(null);
            InvitationGuestActionsPage guestPage = new InvitationGuestActionsPage();

            // Try to accept nonexistent message.
            getDriver().get(getUtil().getURL("Invitation", "InvitationGuestActions", "view", 
                                                "doAction_accept&messageID=12345"));
            Assert.assertTrue("Guests able to accept nonexistent invitation", guestPage.getMessage() != null);
            Assert.assertTrue("Guests trying to accept nonexistent invitation get wrong error message\nMessage: "
                              + guestPage.getMessage(),
                guestPage.getMessage().equals("No message was found by that ID, maybe it was deleted "
                                              + "accidentally or the system is experiencing problems."));

            // Try to decline nonexistent message.
            getDriver().get(getUtil().getURL("Invitation", "InvitationGuestActions", "view", 
                                                "doAction_decline&messageID=12345"));
            Assert.assertTrue("Guests able to decline nonexistent invitation", guestPage.getMessage() != null);
            Assert.assertTrue("Guests trying to decline nonexistent invitation get wrong error message\nMessage: "
                              + guestPage.getMessage(),
                guestPage.getMessage().equals("No invitation was found by the given ID. It might have been deleted or "
                                              + "maybe the system is experiencing difficulties."));

            // Try to report nonexistent message.
            getDriver().get(getUtil().getURL("Invitation", "InvitationGuestActions", "view", 
                                                "doAction_report&messageID=12345"));
            Assert.assertTrue("Guests able to report nonexistent invitation as spam", guestPage.getMessage() != null);
            Assert.assertTrue("Guests trying to report nonexistent invitation as spam get incorrect message\nMessage: "
                              + guestPage.getMessage(),                                
                guestPage.getMessage().equals("There was no message found by the given ID. Maybe an administrator "
                                              + "deleted the message from our system."));
        } finally {
            getUtil().setSession(s);
        }
    }

    @Test
    public void testSendMailToTwoAddresses() throws Exception
    {
        try {
            startGreenMail();
            getSenderPage().fillForm("user@localhost.localdomain anotheruser@localhost.localdomain", null, null);
            InvitationSenderPage.InvitationSentPage sent = getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 2);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();

            Map<String, String> messageA = getMessageContent(messages[0]);
            Map<String, String> messageB = getMessageContent(messages[1]);
            Assert.assertTrue(messageA.get("recipient").contains("user@localhost.localdomain"));
            Assert.assertTrue(messageB.get("recipient").contains("anotheruser@localhost.localdomain"));

            assertMessageValid(messageA);
            assertMessageValid(messageB);

            // Check that the page has the table and the messages.
            Assert.assertTrue(sent.getMessageBoxContent().contains("Your message has been sent."));
            TableElement table = sent.getTable();
            Assert.assertTrue(table.numberOfRows() == 3);
            Assert.assertTrue(table.numberOfColumns() == 3);
            Assert.assertTrue(table.getRow(1).get(1).getText().contains("user@localhost.localdomain"));
            Assert.assertTrue(table.getRow(1).get(2).getText().contains("Pending"));
            Assert.assertTrue(table.getRow(2).get(1).getText().contains("anotheruser@localhost.localdomain"));
            Assert.assertTrue(table.getRow(2).get(2).getText().contains("Pending"));
        } finally {
            stopGreenMail();
        }
    }

    @Test
    public void testPreviewMessage()
    {
        InvitationMessageDisplayElement preview = getSenderPage().preview();
        Assert.assertTrue(preview.getSubjectLine().contains("has invited you to join"));
        Assert.assertTrue(preview.getMessageBody().contains("If this message looks like abuse of our system"));
        Assert.assertTrue(preview.getValidRecipients().get(0).getText().contains("user@localhost.localdomain"));
    }

    @Test
    public void testNonAdminCanSend() throws Exception
    {
        TestUtils.Session s = getUtil().getSession();
        try {
            getUtil().setSession(null);
            getUtil().registerLoginAndGotoPage("NonMailAdminUser", "WeakPassword", getSenderPage().getURL());
            startGreenMail();
            getSenderPage().fillForm("user@localhost.localdomain", null, null);
            InvitationSenderPage.InvitationSentPage sent = getSenderPage().send();

            // Prove that the message was sent.
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            Map<String, String> message = getMessageContent(messages[0]);
            Assert.assertTrue(message.get("recipient").contains("user@localhost.localdomain"));
            assertMessageValid(message);

            // Check that the page has the table and the message.
            Assert.assertTrue(sent.getMessageBoxContent().contains("Your message has been sent."));
            TableElement table = sent.getTable();
            Assert.assertTrue(table.numberOfRows() == 2);
            Assert.assertTrue(table.numberOfColumns() == 3);
            Assert.assertTrue(table.getRow(1).get(1).getText().contains("user@localhost.localdomain"));
            Assert.assertTrue(table.getRow(1).get(2).getText().contains("Pending"));
        } finally {
            stopGreenMail();
            getUtil().setSession(s);
            getUtil().deletePage("XWiki", "NonMailAdminUser");
        }
    }

    /** 
     * This test proves that:
     * 1. Non administrators trying to send to multiple email addresses without permission will get an error message.
     *    and said mail will not be sent.
     * 2. After permission is granted sending to multiple users will work and message will say mail was sent.
     */
    @Test
    public void testUnpermittedUserCannotSendToMultipleAddresses() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            getUtil().setSession(null);
            getUtil().registerLoginAndGotoPage("NonMailAdminUser", "WeakPassword", getSenderPage().getURL());
            startGreenMail();
            getSenderPage().fillForm("user@localhost.localdomain anotheruser@localhost.localdomain", null, null);
            InvitationSenderPage.InvitationSentPage sent = getSenderPage().send();
            getGreenMail().waitForIncomingEmail(2000, 2);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            Assert.assertTrue("Messages were recieved when they shouldn't have been sent!", messages.length == 0);
            Assert.assertTrue("User was not shown the correct error message.",
                sent.getMessageBoxContent().equals("Your message couldn't be sent because there were no valid email "
                                                   + "addresses to send to."));
            stopGreenMail();

            // Become admin and allow users to send to multiple.
            TestUtils.Session nonAdmin = getUtil().getSession();
            getUtil().setSession(admin);
            AdminSectionPage config = new AdminSectionPage("Invitation");
            config.gotoPage();
            config.getForm().setFieldValue(By.id("Invitation.InvitationConfig_Invitation.WebHome_0_"
                                                 + "usersMaySendToMultiple"), "true");
            config.clickSave();
            getUtil().setSession(nonAdmin);

            // Prove that the user can now send to multiple recipients.
            startGreenMail();
            getSenderPage().gotoPage();
            getSenderPage().fillForm("user@localhost.localdomain anotheruser@localhost.localdomain", null, null);
            sent = getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 2);
            messages = getGreenMail().getReceivedMessages();
            Assert.assertTrue("Non admins cannot send mail to even with permission", messages.length == 2);
            Assert.assertTrue("User was not given the message that their mail was sent.",
                sent.getMessageBoxContent().equals("Your message has been sent."));
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);
            getUtil().deletePage("XWiki", "NonMailAdminUser");
        }
    }

    /** 
     * This test proves that:
     * 1. Guests (mail recipients) can report spam.
     * 2. After a spam report, a user's mail privilege is suspended.
     * 3. An admin will see a message telling him that a spam report was made.
     * 4. After an admin marks the message as not spam, the sender can again send mail.
     */
    @Test
    public void testSpamReporting() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            getUtil().setSession(null);
            getUtil().registerLoginAndGotoPage("spam", "andEggs", getSenderPage().getURL());
            startGreenMail();
            getSenderPage().fillForm("undisclosed-recipients@localhost.localdomain", null, 
                                     "You have won the email lottery!");
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            String htmlMessage = getMessageContent(messages[0]).get("htmlPart");

            // Restare greenmail to clear message
            stopGreenMail();
            startGreenMail();

            // Now switch to guest.
            TestUtils.Session spammer = getUtil().getSession();
            getUtil().setSession(null);

            InvitationGuestActionsPage guestPage = 
                new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.REPORT);
            guestPage.setMemo("It's the email lottery, they have taken over your server!");
            guestPage.confirm();
            Assert.assertTrue("Failed to report spam",
                guestPage.getMessage().contains("Your report has been logged and the situation"));

            // Prove that a reported message cannot be accepted (which would clear the "reported" status)
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertTrue("After a message is reported a user can accept it, clearing the spam report",
               guestPage.getMessage().equals("This invitation has been reported as spam and is no longer valid."));
            // Prove that a reported message cannot be declined
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.DECLINE);
            Assert.assertTrue("After a message is reported a user can decline it, clearing the spam report",
                                  guestPage.getMessage().equals("This invitation has already been reported as "
                                                                + "spam and thus cannot be declined."));
            // Switch to admin
            getUtil().setSession(admin);
            // Go to invitation sender.
            getSenderPage().gotoPage();
            // Switch back to spammer.
            getUtil().setSession(spammer);
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(2000, 1);
            Assert.assertTrue("Reported spammers can send mail!", getGreenMail().getReceivedMessages().length == 0);
            Assert.assertTrue("No message telling user he's reported spammer.", getSenderPage().userIsSpammer());

            // Switch to admin.
            getUtil().setSession(admin);
            getSenderPage().gotoPage();
            Assert.assertTrue("No warning in footer that a message is reported as spam",
                              getSenderPage().getFooter().spamReports() == 1);
            // View spam message.
            InspectInvitationsPage inspectPage = getSenderPage().getFooter().inspectAllInvitations();
            InspectInvitationsPage.OneMessage inspect = 
                inspectPage.getMessageWhere("Subject", "spam has invited you to join localhost");
            // Prove that the memo left by spam reported is shown.
            String expectedMessage = "Reported as spam with message: It's the email lottery, they have taken over "
                                   + "your server!";
            Assert.assertTrue("The message by the spam reporter is not shown to the admin.\nExpecting:"
                              + expectedMessage + "\n      Got:" + inspect.getStatusAndMemo(),
                                  inspect.getStatusAndMemo().equals(expectedMessage));

            String memo = "Actually the email lottery is quite legitimate.";
            String expectedSuccessMessage = "Invitation successfully marked as not spam. Log entry: " + memo;
            // Return their sending privilege.
            String successMessage = inspect.notSpam("Actually the email lottery is quite legitimate.");

            // Make sure the output is correct.
            Assert.assertTrue("Admin got incorrect message after marking invitation as not spam\nExpecting:"
                              + expectedSuccessMessage + "\n      Got:" + successMessage,
                              expectedSuccessMessage.equals(successMessage));
            // Switch back to spammer
            getUtil().setSession(spammer);
            getSenderPage().gotoPage();
            Assert.assertFalse("User permission to send not returned by admin action.", 
                getSenderPage().userIsSpammer());
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);
            getUtil().deletePage("XWiki", "spam");
        }
    }

    /** 
     * This test proves that:
     * 1. A guest can decline an invitation.
     * 2. The message status changes and the footer reflects this.
     * 3. The sender can see the info box seeing the guest's reason for declining.
     * 4. The message history table shows the decline properly.
     * 5. A guest cannot accept a message which has already been declined.
     */
    @Test
    public void testDeclineInvitation() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            startGreenMail();
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            String htmlMessage = getMessageContent(messages[0]).get("htmlPart");
            Assert.assertTrue("New invitation is not listed as pending in the footer.",
                getSenderPage().getFooter().myPendingInvitations() == 1);
            // Now switch to guest.
            getUtil().setSession(null);

            InvitationGuestActionsPage guestPage = 
                new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.DECLINE);
            guestPage.setMemo("I'm not interested thank you.");
            guestPage.confirm();
            Assert.assertTrue("Failed to decline invitation",
                getDriver().getPageSource().contains("This invitation has successfully been declined."));
            // Switch to admin
            getUtil().setSession(admin);
            // Go to invitation sender.
            getSenderPage().gotoPage();
            Assert.assertTrue("Declined invitation is still listed as pending in the footer.",
                getSenderPage().getFooter().spamReports() == 0);

            // View declined invitation.
            InspectInvitationsPage inspectPage = getSenderPage().getFooter().inspectMyInvitations();
            InspectInvitationsPage.OneMessage inspect = 
                inspectPage.getMessageWhere("Status", "Declined");

            Assert.assertTrue("Not showing message box to say the invitation has been declined", 
                inspect.getStatusAndMemo().equals("Declined with message: I'm not interested thank you."));

            // Insure the message history table is correct.
            TableElement messageHistoryTable = inspect.clickMessageHistory();
            List<WebElement> row2 = messageHistoryTable.getRow(2);
            Assert.assertTrue("Message history table not showing correctly.",
                              row2.get(0).getText().equals("Declined"));
            Assert.assertTrue("Message history table not showing correctly.",
                              row2.get(2).getText().equals("I'm not interested thank you."));

            //Make sure a guest can't accept the invitation now.
            getUtil().setSession(null);
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertTrue("After a message is declined a user can still accept it!",
               guestPage.getMessage().equals("This invitation has been declined and cannot be accepted now."));
            // Try to decline the invitation.
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.DECLINE);
            Assert.assertTrue("User was allowed to decline an invitation twice.",
                                  guestPage.getMessage().equals("This invitation has already been declined and "
                                                                + "cannot be declined again."));
            // Prove that the message can still be reported as spam
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.REPORT);
            Assert.assertTrue("After the invitation was declined it now cannot be reported as spam.",
                                  guestPage.getMessage().equals(""));
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);
        }
    }

    /**
     * This test proves that:
     * 1. The accept invitation link sent in the email will work.
     * 2. A user can accept an invitation and be directed to the registration form and can register and login.
     * 3. An invitation once accepted cannot be accepted again nor declined.
     * 4. An invitation once accepted can still be reported as spam.
     */
    @Test
    public void testAcceptInvitation() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            startGreenMail();
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            String htmlMessage = getMessageContent(messages[0]).get("htmlPart");
            Assert.assertTrue("New invitation is not listed as pending in the footer.",
                getSenderPage().getFooter().myPendingInvitations() == 1);
            // Now switch to guest.
            getUtil().setSession(null);

            InvitationGuestActionsPage guestPage = 
                new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertTrue("There was an error message when accepting the invitation message:\n"
                              + guestPage.getMessage(), 
                                  guestPage.getMessage().equals(""));
            // Register a new user.
            RegisterPage rp = new RegisterPage();
            rp.fillRegisterForm(null, null, "InvitedMember", "WeakPassword", "WeakPassword", null);
            rp.clickRegister();
            Assert.assertTrue("There were failure messages when registering.",
                                  rp.getValidationFailureMessages().isEmpty());
            getDriver().get(getUtil().getURLToLoginAs("InvitedMember", "WeakPassword"));

            Assert.assertTrue("Failed to log user in after registering from invitation.", rp.isAuthenticated());

            // Now switch to guest again and try to accept the invitation again.
            getUtil().setSession(null);
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertTrue("After the invitation was accepted a user was allowed to accept it again.",
                                  guestPage.getMessage().equals("This invitation has already been accepted and the "
                                                                + "offer is no longer valid."));
            // Try to decline the invitation.
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.DECLINE);
            Assert.assertTrue("After the invitation was accepted a user was allowed to decline it.",
                                  guestPage.getMessage().equals("This invitation has already been accepted and "
                                                                + "now cannot be declined."));
            // Prove that the message can still be reported as spam
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.REPORT);
            Assert.assertTrue("After the invitation was accepted it now cannot be reported as spam.",
                                  guestPage.getMessage().equals(""));
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);
        }
    }

    /**
     * This test proves that:
     * 1. A guest cannot register if register permission is removed from XWikiPreferences.
     * 2. Upon recieving an email invitation the guest can register even without register premission.
     */
    @Test
    public void testAcceptInvitationToClosedWiki() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            // First we ban anon from registering.
            EditObjectsPage eop = new EditObjectsPage();
            getDriver().get(eop.getURL("XWiki", "XWikiPreferences"));

            eop.getObjectsOfClass("XWiki.XWikiGlobalRights").get(2)
                .getSelectElement(By.name("XWiki.XWikiGlobalRights_2_levels")).unSelect("register");

            eop.clickSaveAndContinue();
            // now prove anon cannot register
            getUtil().setSession(null);
            new RegisterPage().gotoPage();
            getUtil().assertOnPage(getUtil().getURL("XWiki", "XWikiLogin", "login"));

            // Now we try sending and accepting an invitation.
            getUtil().setSession(admin);
            getSenderPage().gotoPage();
            senderPage.fillInDefaultValues();

            startGreenMail();
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            String htmlMessage = getMessageContent(messages[0]).get("htmlPart");
            Assert.assertTrue("New invitation is not listed as pending in the footer.",
                getSenderPage().getFooter().myPendingInvitations() == 1);
            // Now switch to guest.
            getUtil().setSession(null);

            InvitationGuestActionsPage guestPage = 
                new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertTrue("There was an error message when accepting the invitation message:\n"
                              + guestPage.getMessage(), 
                                  guestPage.getMessage().equals(""));
            // Register a new user.
            RegisterPage rp = new RegisterPage();
            rp.fillRegisterForm(null, null, "AnotherInvitedMember", "WeakPassword", "WeakPassword", null);
            rp.clickRegister();
            Assert.assertTrue("There were failure messages when registering.",
                                  rp.getValidationFailureMessages().isEmpty());
            getDriver().get(getUtil().getURLToLoginAs("AnotherInvitedMember", "WeakPassword"));

            Assert.assertTrue("Failed to log user in after registering from invitation.", rp.isAuthenticated());
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);

            // Better open the wiki back up again.
            EditObjectsPage eop = new EditObjectsPage();
            getDriver().get(eop.getURL("XWiki", "XWikiPreferences"));

            eop.getObjectsOfClass("XWiki.XWikiGlobalRights").get(2)
                .getSelectElement(By.name("XWiki.XWikiGlobalRights_2_levels")).select("register");

            eop.clickSaveAndContinue();
        }
    }

    /**
     * This test proves that:
     * 1. A user can cancel an invitation after sending it, leaving a message for the recipient should they try to 
     *    accept.
     * 2. A canceled invitation cannot be accepted and the guest will see an explaination with the message left when
     *    the sender canceled.
     * 3. A canceled invitation cannot be declined, the guest gets the sender's note.
     * 4. A canceled invitation can still be reported as spam.
     */
    @Test
    public void testCancelInvitation() throws Exception
    {
        TestUtils.Session admin = getUtil().getSession();
        try {
            startGreenMail();
            getSenderPage().send();
            getGreenMail().waitForIncomingEmail(10000, 1);
            MimeMessage[] messages = getGreenMail().getReceivedMessages();
            String htmlMessage = getMessageContent(messages[0]).get("htmlPart");
            Assert.assertTrue("New invitation is not listed as pending in the footer.",
                getSenderPage().getFooter().myPendingInvitations() == 1);

            InspectInvitationsPage.OneMessage message = getSenderPage().getFooter().inspectMyInvitations()
                .getMessageWhere("Subject", "Admin has invited you to join localhost This is a subject line.");

            InvitationActionConfirmationElement confirm = message.cancel();

            Assert.assertTrue("Confirmation field for canceling invitation has wrong label",
                confirm.getLabel().equals("Leave a message in case the invitee(s) try to register."));

            confirm.setMemo("Sorry, wrong email address.");
            Assert.assertTrue("User not shown the correct message after confirming cancellation of invitation.",
                confirm.confirm().equals("Invitation successfully canceled."));

            // Now switch to guest.
            getUtil().setSession(null);

            String commonPart = "\nAdministrator left you this message when canceling the invitation.\n"
                              + "Sorry, wrong email address.";

            // Prove that invitation cannot be accepted
            InvitationGuestActionsPage guestPage = 
                new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.ACCEPT);
            Assert.assertFalse("Guest was able to accept a message which had been canceled.",
                guestPage.getMessage().equals(""));
            Assert.assertTrue("Guest attempting to accept invitation was not given message that was canceled.",
                guestPage.getMessage().equals("We're sorry but this invitation has been canceled." + commonPart));

            // Prove that invitation cannot be declined
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.DECLINE);
            Assert.assertFalse("Guest was able to decline a message which had been canceled.",
                guestPage.getMessage().equals(""));
            Assert.assertTrue("Guest attempting to decline invitation was not given message that was canceled.",
                guestPage.getMessage().equals("This invitation has been canceled and thus cannot be declined." 
                                              + commonPart));

            // Prove that the message report spam page still shows up.
            guestPage = new InvitationGuestActionsPage(htmlMessage, InvitationGuestActionsPage.Action.REPORT);
            Assert.assertTrue("Guest was not able to report canceled invitation as spam",
                guestPage.getMessage().equals(""));
            guestPage.setMemo("Canceled message is spam.");
            Assert.assertTrue(guestPage.confirm().equals("Your report has been logged and the situation will "
                                + "be investigated as soon as possible, we apologize for the inconvenience."));
        } finally {
            stopGreenMail();
            getUtil().setSession(admin);
        }
    }

    //-----------------------Helper methods--------------------------//


    /** To put the page someplace else, subclass this class and change this method. */
    protected InvitationSenderPage newSenderPage()
    {
        return new InvitationSenderPage();
    }

    protected void assertMessageValid(Map<String, String> message)
    {
        Assert.assertTrue(message.get("htmlPart").contains("If this message looks like abuse of our system"));
        Assert.assertTrue(message.get("subjectLine").contains("has invited you to join"));
    }

    protected Map<String, String> getMessageContent(MimeMessage message) throws Exception
    {
        Map<String, String> messageMap = new HashMap<String, String>();

        Address[] addresses = message.getAllRecipients();
        Assert.assertTrue(addresses.length == 1);
        messageMap.put("recipient", addresses[0].toString());

        messageMap.put("subjectLine", message.getSubject());

        Multipart mp = (Multipart) message.getContent();

        BodyPart plain = getPart(mp, "text/plain");
        if (plain != null) {
            messageMap.put("textPart", plain.getContent().toString());
        }
        BodyPart html = getPart(mp, "text/html");
        if (html != null) {
            messageMap.put("htmlPart", html.getContent().toString());
        }

        return messageMap;
    }

    protected BodyPart getPart(Multipart messageContent, String mimeType) throws Exception
    {
        for (int i = 0; i < messageContent.getCount(); i++) {
            BodyPart part = (BodyPart) messageContent.getBodyPart(i);

            if (part.isMimeType(mimeType)) {
                return part;
            }
            
            if (part.isMimeType("multipart/related") 
                || part.isMimeType("multipart/alternative")
                || part.isMimeType("multipart/mixed")) {
                BodyPart out = getPart((Multipart) part.getContent(), mimeType);
                if (out != null) {
                    return out;
                }
            }
        }
        return null;
    }

    protected void startGreenMail() throws Exception
    {
        this.greenMail = new GreenMail();
        this.greenMail.start();
    }

    protected void stopGreenMail() throws Exception
    {
        if (getGreenMail() != null) {
            getGreenMail().stop();
        }
    }

    protected GreenMail getGreenMail()
    {
        return this.greenMail;
    }

    protected InvitationSenderPage getSenderPage()
    {
        return senderPage;
    }
}
