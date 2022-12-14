# Table of contents

- [Introduction](#introduction)
- [Code Setup](#code-setup)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Configurations](#configurations)
  - [Required Configuration Summary](#required-configuration-summary)
  - [Properties](#properties)
  - [Java VM Options](#java-vm-options)
  - [Java KeyStore](#java-keystore)
- [Code Examples](#code-examples)
  - [Dynamic Client Registration](#dynamic-client-registration)
    - [Dynamic Client Registration](#dynamic-client-registration---registration)
  - [Accounts and Transactions](#accounts-and-transactions)
    - [Accounts and Transactions - OAuth2 Consent Flow](#accounts-and-transactions---oauth2-consent-flow)
    - [Accounts And Transactions - Get Accounts](#accounts-and-transactions---get-accounts)
    - [Accounts And Transactions - Get Account](#accounts-and-transactions---get-account)
    - [Accounts And Transactions - Get Transactions](#accounts-and-transactions---get-transactions)
    - [Accounts And Transactions - Get Beneficiaries](#accounts-and-transactions---get-beneficiaries)
    - [Accounts And Transactions - Get Standing Orders](#accounts-and-transactions---get-standing-orders)
    - [Accounts And Transactions - Get Balances](#accounts-and-transactions---get-balances)
    - [Accounts And Transactions - Get Direct Debits](#accounts-and-transactions---get-direct-debits)
    - [Accounts And Transactions - Get Offers](#accounts-and-transactions---get-offers)
    - [Accounts And Transactions - Get Product](#accounts-and-transactions---get-product)
    - [Accounts And Transactions - Get Scheduled Payments](#accounts-and-transactions---get-scheduled-payments)

# Introduction

This project is a set of examples for consuming OpenBanking APIs from BankOfApis.com. It works across all
brands and is intended to be a set of distinct examples for consuming each of the APIs.

This example code is not intended for production use. It is intended to be the simplest and clearest example
to accelerate your development in making use of OpenBanking Apis.

# Code Setup

- **Java Versions:** 1.8 -  All the code examples require a minimum of Java 8, and have been tested 
against the oracle JVM, although we see no reason why later versions of Java wouldn't work.

- **Maven:** Any - All the code uses maven for build, unit testing, integration testing, and managing dependencies. 
There is a single POM file located within the root of the project folder.

Currently, the only way to build these code examples is by compiling the entire project using `mvn compile`
or manually compiling classes with `javac`. In future there will be options to build specific examples.

# Getting Started

1. Copy `example.properties` as `dev.properties` in `src/main/resources` and fill in key values as per [Properties](#properties) section.
2. Create Java KeyStore containing client TLS certificate in `src/main/resources` as per [Java KeyStore](#java-keystore) section.
3. (**If using IntelliJ**) Set up VM options as per [Java VM Options](#java-vm-options) section
   - Otherwise, ensure you use the VM options as specified when running examples from the command line.
4. \[Optionally\] Run tests using instructions [below](#running-tests) to verify the internal integrity of the codebase.
5. Run the [Dynamic Client Registration](#dynamic-client-registration) example to register your OAuth2 client.
6. Run the [Accounts and Transactions OAuth2 Consent Flow](#accounts-and-transactions---oauth2-consent-flow) example to create an access token for accessing the other endpoints.
7. Verify that your current working directory now contains `auth_token.json`.
8. You can now run all the remaining examples, the code is set up to handle refreshing access tokens for customer consent.

# Running Tests

Tests use JUnit5 and WireMock, and can be run through IntelliJ by right-clicking on the `test/java` directory and
selecting `Run 'All Tests'`, or doing the same for individual tests, or by through maven by running `mvn test` in a terminal.

No additional setup is required for running the tests.

`src/test/resources/test_data/jks/generate-certs.sh` was used to generate test client and server certs,
the certificates generated by this script are required for testing MTLS connection through the `MtlsHttpClient` class.


# Configurations

## Required Configuration Summary

There are three places where configuration is required in order for the examples to run.
Further to this some files may be created dynamically where examples are run e.g. Accounts
and transactions consent flow, that are needed for other examples to function. 

The main configuration files and their purposes are listed below:

file | purpose 
--- | --- 
Properties Files | Used to store configurations required for all examples 
Java VM Options | Used to store privileged information e.g. passwords, which would not be sensible to store within a file
Java Key Store | Used to store client TLS certificates \(see [Oracle's KeyTool documentation](https://docs.oracle.com/cd/E19798-01/821-1751/ghlgv/index.html) for further information on generating a Java KeyStore and signing certificates\)

## Properties

__Location:__ ./src/resources/dev.properties

__Template:__ ./src/resources/example.properties

__You will need to configure your own dev.properties files in order for these examples to run.__
A template for the expected properties are provided in the location above.

The following properties are use by these examples, and their meanings are listed below:

Value | purpose | Acceptable Values
--- | --- | ---
`brand` | Signal brand used when running an example | One of: NWB, UBN, UBR, or RBS
`api_base_url` | Base URL for API Calls (including protocol, no trailing slashes) | A string with the base URL for the API being called (e.g. `https://api.natwest.com`)
`api_require_proxy` | Enable/disable proxy for calls to `api_base_url` |`true` or `false` (defaults to `false`)
`auth_token_base_url` | Base URL for authentication calls (i.e. to get bearer token) (including protocol, no trailing slashes) | A string with the base URL for the OAuth2 token authentication server (e.g. `https://secure1t.natwest.com`)
`auth_token_require_proxy`| Enable/disable proxy for calls to `auth_token_base_url` |`true` or `false` (defaults to `false`)
`auth_consent_base_url` | Base URL for authentication consent calls (i.e. to get oauth2 authorization) (including protocol, no trailing slashes) | A string with the base URL for OAuth2 consent authorization calls (e.g. `https://secure1t.natwest.com`)
`auth_consent_require_proxy`| Enable/disable proxy for calls to `auth_consent_base_url` | `true` or `false` (defaults to `false`)
||
| **Dynamic Client Registration properties** |
`ssa` | Software Statement provided by onboarding team | A signed JWT of the form \<HEADER\>.\<CLAIMS\>.\<SIGNATURE\>
`signing_kid` | The signing key used to sign your SSA, should be provided by onboarding team. | An alphanumeric string.
||
|**Connection/TLS Properties**|
`private_key` | The key used to sign JWTs for client authentication | A multi-line private key starting with  `-----BEGIN RSA PRIVATE KEY-----` and ending with `-----END RSA PRIVATE KEY-----`.  Each line should end with `\n\ ` bar the last.  The key can also begin and end with `-----BEGIN PRIVATE KEY-----` and end with `-----END PRIVATE KEY-----` depending on the format used to generate the key.
`Keystore_path` | The relative path (within `src/main/resources`) of the `.jks` keystore. | A string ending in `.jks` (e.g. `keystore.jks`).
`Keystore_password` | The password for the `jks` specified in `keystore_path`. | A string value for the password for the relevant `.jks` file (e.g. `changeme`).
`proxy_host` | The hostname (if any) for a proxy connection. | A string value for the hostname of a proxy server (e.g. `my-proxy.com`)
`proxy_port` | The port on which to access the `proxy_host` | An integer value for the proxy port (e.g. `8080`)
`proxy_scheme` | The scheme for the proxy specified in `proxy_host` | Either `http` or `https`
||
|**OAuth2 Properties**|
`client_id` | The client ID for your OAuth2 client, should be provided by onboarding team. | An alphanumeric string, e.g. `abcdefABCDEF0123456rbs`.
`client_secret`| The client secret (if any) specific to your OAuth2 Client, should be provided by onboarding team | A random or pseudorandom string in line with OAuth2 standard.
`scope` | The scope for your OAuth2 client | A space-separated list of scopes, specified via Open Banking (e.g. `accounts payments customer:read`)
`redirect_uri` | The redirect URI to be used when a user authorizes the client, should be provided to onboarding team as part of onboarding so it can be whitelisted. | A string with the URI which will handle the authorization code as part of OAuth2 spec. (e.g. `https://localhost:8080/print_redirect_uri.html`)

## Java VM Options

All of these should be supplied via VM options (e.g. `-Dhttp.proxyUser=username`)

Value | purpose | acceptable values
--- | --- | ---
`http.proxyUser` | Stores your username for user when running requests via proxy. | A string with your proxy username
`http.proxyPassword` | Stores your proxy user's password. | A string with your proxy password
(Optional) `nt.domain` | Stores your proxy user's NT domain. | A string with your NT user's domain

## Java KeyStore

Your JKS should contain a certificate for your OAuth2 client which can be used as your signed client TLS certificate.
If you don't already have one, create a JKS, create a CSR, get the CSR signed by your certification authority
(check with onboarding team to verify signing requirements), and import the signed certificate to your JKS.

For reference, your JKS alias should match your client ID, see [Oracle's KeyTool documentation](https://docs.oracle.com/cd/E19798-01/821-1751/ghlgv/index.html)
for further information on generating a Java KeyStore and signing certificates.

# Code Examples

## Dynamic Client Registration

### Dynamic Client Registration - Registration

* #### Summary

This examples shows how to perform dynamic client registration for a third-party OAuth2 client.

* #### Prerequisites

  - SSA provided by onboarding team
  - OAuth2 client details provided by onboarding team
  - Valid properties file as detailed above

* #### Example Specific Configurations

There are no specific configurations for this example (beyond anything required by the prerequisites)

* #### Running Instructions

__Main Class:__ `com.bankofapis.dynamic_client_registration.DynamicRegistrationExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `DynamicRegistrationExample` navigate to `DynamicRegistrationExample` within `src/main/java/com/bankofapis/dynamic_client_registration`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/dynamic_client_registration/DynamicRegistrationExample

## Accounts and Transactions

### Accounts and Transactions - OAuth2 Consent Flow

* #### Summary

This example shows how to get customer consent for accessing account information. This assumes that the OAuth2
client is registered via the dynamic client registration endpoint prior to running. On completion this example
creates `auth_token.json` which allows the proceeding examples to run without requiring additional authorisation.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)

* #### Example Specific Configurations

There are no specific configurations for this example (beyond anything required by the prerequisites)

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountsExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `OAuth2ConsentFlowExample` navigate to `OAuth2ConsentFlowExample` within
`src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/OAuth2ConsentFlowExample

* ##### Authentication From IDE or Terminal

When running the program the terminal will display the following message with a link for authentication:

> To perform consent, go to this link and log in.

This link must be followed and valid customer credentials for the selected brand/environment
supplied for the program to receive a valid response.  The link which you are redirected to should be copied and
pasted directly into the terminal \(ensure that the link contains a query parameter for `code=`, if not you may need
to check your browser history to find the proper link\).

Once consent is complete the program will save `auth_token.json` to the runtime directory which allows
the rest of the examples to run without re-consenting each time, making use of the refresh token if the
access token expires.

### Accounts And Transactions - Get Accounts

* #### Summary

This example will show how to get the account(s) of a specific customer. It assumes that consent for accessing the
customers accounts has already been provided and that you have already been through dynamic client registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)

* #### Example Specific Configurations

There are no specific configurations for this example (beyond anything required by the prerequisites)

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountsExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountsExample` navigate to `GetAccountsExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options. 

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountsExample

### Accounts And Transactions - Get Account

* #### Summary

This example will show how to get a single account for a specific customer. It assumes that consent for accessing the
customers accounts has already been provided and that you have already been through dynamic client registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountExamaple`

* ##### Running From Within Your IDE
__Instructions below are specific to IntelliJ__

To run the `GetAccountExample` navigate to `GetAccountExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountExample -accountId <account ID>

add the following to the program arguments box:

    -accountId <an account ID>

### Accounts And Transactions - Get Transactions

* #### Summary

This example will show how to get the transactions of an account for a specific customer. It assumes that consent for
accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountTransactionsExample`

* ##### Running From Within Your IDE
__Instructions below are specific to IntelliJ__
To run the `GetAccountTransactionsExample` navigate to `GetAccountTransactionsExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountTransactionsExample -accountId <account ID>


### Accounts And Transactions - Get Beneficiaries

* #### Summary

This example will show how to get the beneficiaries of an account for a specific customer. It assumes that consent for
accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountBeneficiariesExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountBeneficiariesExample` navigate to `GetAccountTransactionsExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountTransactionsExample -accountId <account ID>


### Accounts And Transactions - Get Standing Orders

* #### Summary

This example will show how to get the standing orders for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountStandingOrdersExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountStandingOrdersExample` navigate to `GetAccountStandingOrdersExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountStandingOrdersExample -accountId <account ID>


### Accounts And Transactions - Get Balances

* #### Summary

This example will show how to get the balances for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountBalancesExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountStandingOrdersExample` navigate to `GetAccountBalancesExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountBalancesExample -accountId <account ID>


### Accounts And Transactions - Get Direct Debits

* #### Summary

This example will show how to get the direct debits for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountDirectDebitsExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountStandingOrdersExample` navigate to `GetAccountDirectDebitsExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountDirectDebitsExample -accountId <account ID>


### Accounts And Transactions - Get Offers

* #### Summary

This example will show how to get the offers for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

***NOTE: This example is currently disabled due to instability.*** 

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountOffersExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountOffersExample` navigate to `GetAccountOffersExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountOffersExample -accountId <account ID>


### Accounts And Transactions - Get Product

* #### Summary

This example will show how to get the product for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountProductExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountProductExample` navigate to `GetAccountProductExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountProductExample -accountId <account ID>


### Accounts And Transactions - Get Scheduled Payments

* #### Summary

This example will show how to get the scheduled payments for an account for a specific customer. It assumes that consent
for accessing the customers accounts has already been provided and that you have already been through dynamic client
registration.

* #### Prerequisites

  - Dynamic client registration (`DynamicRegistrationExample`)
  - Consent has been provided for the customer (`OAuth2ConsentFlowExample`)
  - An account ID (can be gathered from output of [Get Accounts](#accounts-and-transactions---get-accounts))

* #### Example Specific Configurations

Value | Purpose | Example Values
--- | --- | ---
accountId | Specify the account you wish to query in this example | -accountId 12345678

* #### Running Instructions

__Main Class:__ `com.bankofapis.account_and_transactions.GetAccountScheduledPaymentsExample`

* ##### Running From Within Your IDE

__Instructions below are specific to IntelliJ__

To run the `GetAccountScheduledPaymentsExample` navigate to `GetAccountScheduledPaymentsExample`
within `src/main/java/com/bankofapis/account_and_transactions`

The example contains a main method that can be run from the IDE, either through the play/run buttons in the line column,
or via toolbar options.

VM options configuration must be added in order for the program to run.

To add vm options go to:

`Edit Configurations>Build and Run>modify options>add vm options`

add the following to the VM options box:

    -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> -Dnt.Domain=<your user's NT domain domain (optional)>

add the following to the program arguments box:

    -accountId <an account ID>

* ##### Running From Terminal

The following two commands should compile all the code examples and run the relevant class

    mvn compile
    java -Dhttp.proxyUser=<your username> -Dhttp.proxyPassword=<your password> [-Dnt.Domain=<your user's NT domain domain>] target/classes/com/bankofapis/account_and_transactions/GetAccountScheduledPaymentsExample -accountId <account ID>
