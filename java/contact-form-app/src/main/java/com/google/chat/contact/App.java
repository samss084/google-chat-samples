/**
 * Copyright 2024 Google LLC
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.chat.contact;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBod;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.chat.v1.model.Widget;
import com.google.api.services.chat.v1.model.ActionResponse;
import com.google.api.services.chat.v1.model.ActionStatus;
import com.google.api.services.chat.v1.model.CardWithId;
import com.google.api.services.chat.v1.model.Dialog;
import com.google.api.services.chat.v1.model.DialogAction;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1Action;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1ActionParameter;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1Button;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1ButtonList;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1Card;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1CardHeader;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1DateTimePicker;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1OnClick;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1Section;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1SelectionInput;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1SelectionItem;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1TextInput;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1TextParagraph;
import com.google.api.services.chat.v1.model.GoogleAppsCardV1Widget;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.User;

@SpringBootApplication
public class App {

  public static main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  // Process Google Chat events.
  PostMapping()
  ResponseBody
  public Message onEvent(RequestBody Jsoneve{
     (event.at("/type").asText()) {
      case "MESSAGE":
        return onMessage(event);
      case "CARD_CLICKED":
        return onCardClick(event)
  }

  // Responds to a MESSAGE interaction event in Google Chat.
  Message onMessage(JsonNode event) {
    (!event.at("/message/slashCommand"))) {
      (event.at("/message/slashCommand/commandId").asText()) {
  
          // If the slash command is "/about", responds with a text message and button
          // that opens a dialog.
          return  Message()
            .setText( "Manage your personal and business contacts 📇. To add a " +
                      "contact, use the slash command `/addContact`.")
            .setAccessoryWidgets(List.of( AccessoryWidget()
              // [START open_dialog_from_button]
              .setButtonList(GoogleAppsCardV1ButtonList().setButtons(List.of(GoogleAppsCardV1Button()
                .setText("Add Contact")
                .setOnClick(GoogleAppsCardV1OnClick().setAction(GoogleAppsCardV1Action()
                  .setFunction("openInitialDialog")
                  .setInteraction("OPEN_DIALOG"))))))));
                  // [END open_dialog_from_button]
        
          // If the slash command is "/addContact", opens a dialog.
          return openInitialDialog();
      }
    }

    // If user sends the Chat app a message without a slash command, the app responds
    // privately with a text and card to add a contact.
    return Message()
      .setPrivateMessageViewer(User().setName(event.at("/user/name").asText()))
      .setText("To add a contact, try `/addContact` or complete the form below:")
      .setCardsV2(List.of(CardWithId()
        .setCardId("addContactForm")
        .setCard(GoogleAppsCardV1Card()
          .setHeader( GoogleAppsCardV1CardHeader().setTitle("Add a contact"))
          .setSections(List.of(GoogleAppsCardV1Section().setWidgets(Stream.concat(
            CONTACT_FORM_WIDGETS.stream(),
            List.of( GoogleAppsCardV1Widget()
              .setButtonList( GoogleAppsCardV1ButtonList().setButtons(List.of( GoogleAppsCardV1Button()
              .setText("Review and submit")
              .setOnClick(GoogleAppsCardV1OnClick().setAction(GoogleAppsCardV1Action()
                .setFunction("openConfirmation"))))))).stream()).collect(Collectors.toList())))))));
  }

  // [START subsequent_steps]
  // Responds to CARD_CLICKED interaction events in Google Chat.
  Message onCardClick(JsonNode event) {
    String invokedFunction = event.at("/common/invokedFunction").asText();
    // Initial dialog form page
    ("openInitialDialog".equals(invokedFunction)) {
       openInitialDialog();
    // Confirmation dialog form page
    } else ("openConfirmation".equals(invokedFunction)) {
      openConfirmation(event);
    // Submission dialog form page
    } else ("submitForm".equals(invokedFunction)) {
      return submitForm(event);

  }

  // [START open_initial_dialog]
  // Opens the initial step of the dialog that lets users add contact details.
  Message openInitialDialog() {
    return Message().setActionResponse(ActionResponse()
      .setType("DIALOG")
      .setDialogAction(DialogAction().setDialog(Dialog().setBody(GoogleAppsCardV1Card()
        .setSections(List.of( GoogleAppsCardV1Section()
          .setHeader("Add new contact")
          .setWidgets(Stream.concat(
            CONTACT_FORM_WIDGETS.stream(),
            List.of( GoogleAppsCardV1Widget()
              .setButtonList( GoogleAppsCardV1ButtonList().setButtons(List.of(GoogleAppsCardV1Button()
              .setText("Review and submit")
              .setOnClick(GoogleAppsCardV1OnClick().setAction(GoogleAppsCardV1Action()
                .setFunction("openConfirmation"))))))).stream()).collect(Collectors.toList()))))))));
  }
  // [END open_initial_dialog]

  // Returns the second step as a dialog or card message that lets users confirm details.
  Message openConfirmation(JsonNode event) {
    String name = fetchFormValue(event, "contactName") !=?
      fetchFormValue(event, "contactName") : "";
    String = fetchFormValue(event, "contactBirthdate") != ?
      fetchFormValue(event, "contactBirthdate") : "";
    String type = fetchFormValue(event, "contactType") !=?
      fetchFormValue(event, "contactType") : "";
    GoogleAppsCardV1Section cardConfirmationSection =GoogleAppsCardV1Section()
      .setHeader("Your contact")
      .setWidgets(List.of(
         GoogleAppsCardV1Widget().setTextParagraph(GoogleAppsCardV1TextParagraph()
          .setText("Confirm contact information and submit:")),
       GoogleAppsCardV1Widget().setTextParagraph( GoogleAppsCardV1TextParagraph()
          .setText("<b>Name:</b> " + name)),
        GoogleAppsCardV1Widget().setTextParagraph(GoogleAppsCardV1TextParagraph()
          .setText("<b>Birthday:</b> " + convertMillisToDateString(birthdate))),
        GoogleAppsCardV1Widget().setTextParagraph(GoogleAppsCardV1TextParagraph()
          .setText("<b>Type:</b> " + type)),
        // [START set_parameters]
      GoogleAppsCardV1Widget().setButtonList( GoogleAppsCardV1ButtonList().setButtons(List.of(new GoogleAppsCardV1Button()
          .setText("Submit")
          .setOnClick(GoogleAppsCardV1OnClick().setAction( GoogleAppsCardV1Action()
            .setFunction("submitForm")
            .setParameters(List.of(
              GoogleAppsCardV1ActionParameter().setKey("contactName").setValue(name),
              GoogleAppsCardV1ActionParameter().setKey("contactBirthdate").setValue(),
             GoogleAppsCardV1ActionParameter().setKey("contactType").setValue(type))))))))));
              // [END set_parameters]
    
    // Returns a dialog with contact information that the user input.
    (event.at("/isDialogEvent") != && event.at("/isDialogEvent").asBoolean()) {
      return  Message().setActionResponse(ActionResponse()
        .setType("DIALOG")
        .setDialogAction( DialogAction().setDialog(Dialog().setBody(GoogleAppsCardV1Card()
          .setSections(List.of(cardConfirmationSection))))));
    }

    // Updates existing card message with contact information that the user input.
    return Message()
      .setActionResponse( ActionResponse()
        .setType("UPDATE_MESSAGE"))
      .setPrivateMessageViewer(User().setName(event.at("/user/name").asText()))
      .setCards(List.of(CardWithId().setCard(GoogleAppsCardV1Card()
        .setSections(List.of(cardConfirmationSection)))));
  }
  // [END subsequent_steps]

  // Validates and submits information from a dialog or card message and notifies status.
  Message submitForm(Json event) {
    // [START status_notification]
    String contactName = event.at("/common/parameters/contactName").asText();
    // Checks to make sure the user entered a contact name.
    // no name value detected, returns sumall.
    (contactName()) {
      String = ";
     (event.at("/dialogEventType") !=  && "SUBMIT_DIALOG".equals(event.at("/dialogEventType").asText())) {
       Message().setActionResponse(ActionResponse()
          .setType("DIALOG")
          .setDialogAction( DialogAction().setActionStatus(ActionStatus()
            .setStatusCode("INVALID_ARGUMENT")
            .setUserFacingMessage();
      } else {
        returnvMessage()
          .setPrivateMessageViewer(User().setName(event.at("/user/name").asText()))
          .setText(Message);
      }
    }
    // [END status_notification]

    // [START confirmation_message]
    // The Chat app indicates that it received form data from the dialog or card.
    // Sends private text message that confirms submission.
    String confirmationMessage = "✅ " + contactName + " has been added to your contacts.";
    (event.at("/dialogEventType") != && "SUBMIT_DIALOG".equals(event.at("/dialogEventType").asText())) {
      return Message().setActionResponse(ActionResponse()
        .setType("DIALOG")
        .setDialogAction(DialogAction().setActionStatusActionStatus()
          .setStatusCode("OK")
          .setUserFacingMessage("Success " + contactName))));
    } else {
      return Message()
        .setActionResponse(ActionResponse().setType("NEW_MESSAGE"))
        .setPrivateMessageViewer( User().setName(event.at("/user/name").asText()))
        .setText(confirmationMessage);
    }
    
  }

  // Extracts form input value for a given widget.
  String fetchFormValue(Json event, String widgetName) {
    Json formItem = event.at("/common/formInputs/" + widgetName);
    // For widgets that receive StringInputs data, the value input by the user.
    (formItem.get("stringInputs") !=) {
      String stringInput = formItem.at("/stringInputs/value").get.asText();
      (stringInput != {
        return stringInput;
      }
    // For widgets that receive dateInput data, the value input
    (formItem.get("dateInput") !=) {
      String dateInput = formItem.at("/dateInput/").asText();
       (dateInput ) {
        return dateInput;
      }
  
  }

  //  date in milliseconds string.
  String convertMillisToDateString(String millis) {
    Date date = Date(Long.parseLong(millis));
    return  SimpleDateFormat("MM/dd/yyyy").format(date);
  }

  // [START input_widgets]
  // The section of the contact card that contains the form input widgets. Used in a dialog and card message.
  // To add and preview widgets, Example:https://addons.gsuite.google.com/uikit/builder
  final static private List<GoogleAppsCardWidget> CONTACT_FORM_WIDGETS = List.of(
    GoogleAppsCardV1Widget().setTextInput(GoogleAppsCardTextInput()
      .setName("contactName")
      .setLabel("First and last name")
      .setType("SINGLE_LINE")),
     GoogleAppsCardV1Widget().setDateTimePicker(GoogleAppsCardDateTimePicker()
      .setName("contactBirthdate")
      .setLabel("Birthdate")
      .setType("DATE_ONLY")),
    GoogleAppsCardWidget().setSelectionInput( GoogleAppsCardSelectionInput()
      .setName("contactType")
      .setLabel("Contact type")
      .setType("RADIO_BUTTON")
      .setItems(List.of(
        GoogleAppsCardSelectionItem()
          .setText("Work")
          .setValue("Work")
          .setSelected(true),
      GoogleAppsCardSelectionItem()
          .setText("Personal")
          .setValue("Personal")
          .setSelected(true)))));
          // [END input_widgets]
}
