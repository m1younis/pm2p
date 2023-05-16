# pm2p

An asynchronous, peer-to-peer messaging application based on the *"Polite Messaging"* (PM)
protocol. It runs over TCP and is able to act as a client or server, where messages can be stored
and exchanged.

The upcoming subheadings introduce PM in further detail, specifically the structure of message
objects in addition to all valid requests, their nature and responses.

## Messages

Message bodies consist of four or more lines, which are interpreted as headers (some optional)
until the contents are identified. The following are valid PM message headers:

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

Any unrecognised headers are ignored.
[`messages.txt`](https://github.com/m1younis/pm2p/blob/master/src/main/resources/messages.txt)
contains the initial example message which follows the format outlined above.

## Requests & Responses

Communication only occurs via interchanging defined requests once peers agree upon PM. This can be
achieved by sending the three-part protocol acknowledgement request below:

```
ACK? PM/<version> <identifier>
```

No response is returned. `version` is a positive integer indicating the protocol version, the
minimum value of which is 1. `identifier` is a string used to identify the connecting peer. The
remaining request implementations are described as follows:

- `TIME?` – returns the current time at the peer in Unix Epoch, with the response displayed as
<code>NOW <em>time</em></code>.

- `LOAD? <hash>` – retrieves a stored message object associated with the SHA-256 sum `hash`. Given
  the message exists at the peer, the response is shown in the form
  <code>SUCCESS <em>message</em></code>, where both parts are divided by a new line. Otherwise,
  `NOT FOUND` is returned.

- `SHOW? <since> <headers>` – lists the SHA-256 sums of messages created on or after `since`
  (a Unix Epoch time in the past) that contain the contents specified by `headers` (0 or more)
  which gives the number of following lines for the content to match. The response is a combination
  of <code>ENTRIES <em>count</em></code> followed by the resulting hash values on separate lines.
  `NONE` is returned if no messages meet the conditions set.

- `QUIT!` – (politely) ends the communication between two peers without giving a response.

The `HELP?` request provides a summary of all supported requests in a similar fashion to the above.
If an invalid request is made, the sending peer's connection socket is closed and their interaction
ends immediately.
