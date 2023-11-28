# MileStone3
Nicholas Engle and Aidan Collins

 Question 1                 
TEST 1
{
  "status": "FAILURE",
  "message": "User not found"
}
TEST 2
{
  "status": "SUCCESS",
  "message": "Registration successful"
}
TEST 3
{
  "status": "FAILURE",
  "message": "Incorrect password"
}
TEST 4
{
  "status": "SUCCESS",
  "message": "Login successful"
}
TEST 5
{
  "availableUsers": [],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 6
{
  "availableUsers": null,
  "invitation": null,
  "invitationResponse": null,
  "status": "FAILURE",
  "message": "User not logged in"
}
{
  "status": "SUCCESS",
  "message": "Login successful"
}
TEST 7
{
  "availableUsers": [
    {
      "username": "user2",
      "password": "",
      "displayName": "aidan",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 8
{
  "availableUsers": [
    {
      "username": "user1",
      "password": "",
      "displayName": "nick",
      "online": true
    },
    {
      "username": "user3",
      "password": "",
      "displayName": "rick",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 9
{
  "availableUsers": [
    {
      "username": "user1",
      "password": "",
      "displayName": "nick",
      "online": true
    },
    {
      "username": "user3",
      "password": "",
      "displayName": "rick",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 10
{
  "availableUsers": [
    {
      "username": "user1",
      "password": "",
      "displayName": "nick",
      "online": true
    },
    {
      "username": "user3",
      "password": "",
      "displayName": "rick",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 11
{
  "status": "SUCCESS",
  "message": "Invitation sent successfully"
}
TEST 12
{
  "availableUsers": [
    {
      "username": "user1",
      "password": "",
      "displayName": "nick",
      "online": true
    },
    {
      "username": "user3",
      "password": "",
      "displayName": "rick",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": {
    "eventId": 8,
    "sender": "user1",
    "opponent": "user2",
    "status": "PENDING",
    "turn": null,
    "move": -1
  },
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}

TEST 13
{
  "status": "SUCCESS",
  "message": "Invitation declined successfully"
}
TEST 14
{
  "availableUsers": [
    {
      "username": "user2",
      "password": "",
      "displayName": "aidan",
      "online": true
    },
    {
      "username": "user3",
      "password": "",
      "displayName": "rick",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": {
    "eventId": 8,
    "sender": "user1",
    "opponent": "user2",
    "status": "DECLINED",
    "turn": null,
    "move": -1
  },
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 15
{
  "status": "SUCCESS",
  "message": "Response acknowledged successfully"
}
TEST 16
{
  "status": "SUCCESS",
  "message": "Invitation sent successfully"
}
TEST 17
{
  "availableUsers": [
    {
      "username": "user1",
      "password": "",
      "displayName": "nick",
      "online": true
    },
    {
      "username": "user2",
      "password": "",
      "displayName": "aidan",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": {
    "eventId": 9,
    "sender": "user1",
    "opponent": "user3",
    "status": "PENDING",
    "turn": null,
    "move": -1
  },
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 18
{
  "status": "SUCCESS",
  "message": "Invitation accepted successfully"
}
TEST 19
{
  "availableUsers": [
    {
      "username": "user2",
      "password": "",
      "displayName": "aidan",
      "online": true
    },
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": {
    "eventId": 9,
    "sender": "user1",
    "opponent": "user3",
    "status": "ACCEPTED",
    "turn": null,
    "move": -1
  },
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 20
{
  "status": "SUCCESS",
  "message": "Response acknowledged successfully"
}
TEST 21
{
  "availableUsers": [
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}
TEST 22
{
  "status": "FAILURE",
  "message": "Invalid or unauthorized request"
}
TEST 23
{
  "availableUsers": [
    {
      "username": "user4",
      "password": "",
      "displayName": "john",
      "online": true
    }
  ],
  "invitation": null,
  "invitationResponse": null,
  "status": "SUCCESS",
  "message": "Pairing update successful"
}


Question 2
COMPLETE_GAME is used to indicate a normal, successful conclusion of the game, while ABORT_GAME is used to signal an early, intentional termination of the game. The specific logic for handling these requests is implemented in the corresponding handleCompleteGame() and handleAbortGame() methods in the ServerHandler class.

Question 3
No, with the current implementation, two users cannot log in to the system using the same account credentials. The reason is that the currentUsername attribute in the ServerHandler class is an instance variable, and it is shared among all instances of the class. This means that if one user logs in and sets the currentUsername to a specific value, any subsequent user who logs in will overwrite this value.

Question 4
PENDING:
Trigger: An invitation is sent to an opponent.
Description: The event is in the PENDING state when an invitation is sent, waiting for the opponent to respond.
Transition: If the opponent accepts the invitation, the status transitions to ACCEPTED. If the opponent declines, the status transitions to DECLINED.

ACCEPTED:
Trigger: The opponent accepts the invitation.
Description: The event is in the ACCEPTED state when the opponent accepts the invitation, indicating that the game is set to start.
Transition: If the game is completed or aborted, the status transitions to COMPLETED or ABORTED, respectively.

DECLINED:

Trigger: The opponent declines the invitation.
Description: The event is in the DECLINED state when the opponent
Question 5
Clearing the database before starting the server in Part 2 - Task 3 is a common practice in testing environments to ensure a clean and consistent state before running tests. It helps avoid interference from previous test runs and provides a known starting point for the tests.

If the line that clears the database is removed, the test might encounter issues related to the existing data in the database.


