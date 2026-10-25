# TestMu AI Selenium Playground Assignment

This is a Maven + Java + Selenium + TestNG starter project for the three Selenium Playground scenarios.

## Requirements
- JDK 17
- Maven (or Eclipse with Maven support)
- TestMu AI / LambdaTest account credentials

## Configure credentials in Eclipse
Open **Run Configurations** for the TestNG run and add these Environment variables:
- `LT_USERNAME` = your TestMu AI / LambdaTest username
- `LT_ACCESS_KEY` = your access key

Do not hard-code credentials in Java or commit them to GitHub.

## Run
In Eclipse, import this folder as an **Existing Maven Project**, then right-click `PlaygroundTests.java` and choose **Run As > TestNG Test**.

## Important
The form page markup can change. If `inputFormSubmit` fails to find a field, inspect that field on the current page and update its locator. The empty-form check here verifies required-field validation; if your assignment specifically requires the visible text `Please fill in the fields`, adjust the assertion to match the actual page behavior.
