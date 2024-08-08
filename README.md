# pm2p

An asynchronous, peer-to-peer desktop messaging application based on the *"Polite Messaging"* (PM)
protocol originally built for a coursework task. It runs over TCP and is able to act as a client or
server, where messages can be stored and exchanged.

The upcoming subheadings introduce PM in further detail, specifically the structure of message
objects in addition to all valid requests, their nature and responses.

## Messages

Message bodies consist of four or more lines, which are interpreted as headers (some optional)
until the contents are identified. Valid headers include:

- `Message-uid: SHA-256 <hash>` – this is always the first header. `hash` is the unique SHA-256 sum
corresponding to the remaining message headers and contents.

- `Created: <time>` – the time (in Unix Epoch) at which the message object was created.

- `From: <sender>` – identifies who sent the message. This may be an account handle or email
address.

- `To: <recipient>` (optional) – identifies who the message is going to, which may also follow the
same format as `sender`.

- `Topic: <topic>` (optional) – generalises the message contents.

- `Subject: <subject>` (optional) – summarises the message contents.

- `Contents: <lines>` – this appears last in the overall body. `lines` is a positive integer used
to determine how long the message contents are.

Any unrecognised headers are ignored during exchanges. Creating messages can be done through an
allocated panel on the client, where all of the above is accounted for.

`pm2p.db` is the SQLite database file for storing messages, initialised with the testing message
shown in the next's heading preview snippet.

## Requests & Responses

Communication only occurs via interchanging defined requests once peers agree upon PM. This can be
achieved by sending the three-part protocol acknowledgement request below:

```
ACK? PM/<version> <identifier>
```

No response is returned. `version` is a positive integer indicating the protocol version, the
minimum value of which is 1. `identifier` is a string used to identify the connecting peer. The
remaining request implementations are described below.

| Request | Description | Response |
| ---: | --- | :--- |
| `TIME?` | Returns the current time at the peer in Unix Epoch. | <code>NOW <em>time</em></code> |
| `LOAD? <hash>` | Retrieves the stored message object associated with the given SHA-256 sum, `hash`. | <code>SUCCESS <em>message</em></code> is shown if the sum exists, separated by a new line. Otherwise, `NOT FOUND` is displayed. |
| `SHOW? <since> <headers>` | Lists the SHA-256 sums of messages created on or after `since` (a Unix Epoch time in the past) that contain the contents specified by `headers` (0 or more) which gives the number of following lines for the content to match. | A combination of <code>ENTRIES <em>count</em></code> followed by the resulting hash values on separate lines. `NONE` is returned if no messages meet the conditions set. |
| `QUIT!` | (Politely) ends communication between two peers. | *None* |

The `HELP?` request provides a summary of all supported requests in a similar fashion to the above.
If an invalid request is made, the sending peer's connection socket is closed and their interaction
ends immediately.

A preview of the client interface and full example of PM between two peers is shown in the
following snippet.

![](https://github.com/user-attachments/assets/7ef5559b-aa8c-45dc-9c0e-02b57dec5192)

## Configuration

This project was built using Java SE
[11](https://docs.oracle.com/en/java/javase/11/install/overview-jdk-installation.html) and Apache
Maven [3.8.1](https://maven.apache.org/docs/3.8.1/release-notes.html), although no issues should
arise with newer versions. Both can be installed on your specific operating system by following the
inline links.

> [!IMPORTANT]
> If you only have a single machine running Windows, installing
> [Netcat](https://eternallybored.org/misc/netcat/) is the easiest way to emulate peers. Commands
> for achieving this are outlined [here](#netcat-for-windows).

## Execution

Port 1123 listens for incoming peers by default. Launching the app can be done through your
terminal or an IDE that supports Java and Maven, ideally IntelliJ IDEA.

### Terminal

Open your terminal within the root directory or navigate into it, then build and execute via
Maven by entering:

```shell
$  mvn compile & mvn exec:java
```

### IntelliJ IDEA

Follow [this](https://www.jetbrains.com/help/idea/maven-support.html#maven_import_project_start)
short tutorial from JetBrains to open this repository as an existing Maven project to get started.

Ensure that the minimum project SDK is set to Java 11 by referring to
[these](https://www.jetbrains.com/help/idea/sdk.html#change-project-sdk) instructions.

Simply open `App.java` from the project menu then click on the green play-shaped symbol positioned
in the sidebar to run the program.

### Netcat for Windows

Start a new instance of Netcat (or multiple) and enter `localhost 1123` or `127.0.0.1 1123` to
initiate a connection.

Given you want to connect from the client, you'd set a port for Netcat to listen on first by
entering `-l -v -p <port>` then submitting the corresponding details within the dedicated panel.
The IP address field is validated to accept `localhost` and IPv4/32-bit inputs.
