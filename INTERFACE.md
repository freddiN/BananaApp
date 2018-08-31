## JSON Interface 

Beschreibung des JSON Interfaces der Banana App.


### GetAccountDetails

Returned alle Userinfos

| Eingabe | Ausgabe |
| --- | --- |
| Token | alle Userinfos inkl. AD User, Bananen, Token, Token Ablaufzeitpunkt |

#### Request
```
{
  "actionname": "get_account_details",
  "login": {
     "token": "(token)",
    “source”: “(app or website)”
  }
}
```

#### Response
```
{
  "actionname": "get_account_details",
  "status": "login ok",
  "action_result": [
  {
    "display_name": "(vorname)",
    “ad_user": "(nachname)",
    "bananas_to_spend": (anzahl),
    "bananas_received": (anzahl),
    "is_admin": 0
  }
]
}
```

### GetUserList

Liefert eine Liste alle User, allerdings ohne sensible Infos wie AD User oder Token. Genutzt um die Bananenstände aller User abzufragen.

| Eingabe | Ausgabe |
| --- | --- |
| Token | Liste aller User mit Bananenstände |

#### Request
```
{
  "actionname": "get_user_list",
   "login": {
     "token": "(token)",
     “source”: “(app or website)”
  }
}
```

#### Response
```
{
  "actionname": "get_user_list",
  "status": "get_user_list ok",
  "action_result": [
    {
      "id": "(user0_id)",
      "display_name": "(user0_display)",
      "ad_user": "(user0_ad_user)",
      "bananas_to_spend": (anzahl),
      "bananas_received": (anzahl),
      "is_admin": 0
    },
    {
      "id": ""(user1_id)",
      "display_name": "(user1_display)",
      "ad_user": "(user1_aduser)",
      "bananas_to_spend": (anzahl),
      "bananas_received": (anzahl),
      "is_admin": 1
    }
  ]
}
```

### GetTransactionList

Liefert eine Liste aller Transaktionen, aktuell auf 1000 limitiert

| Eingabe | Ausgabe |
| --- | --- |
| Token | Liste aller Transaktionen |

#### Request
```
{
  "actionname": "get_transaction_list",
   "login": {
      "token": "(token)",
      "source": "(app or website)"
  },
  "action_request": {
       "limit": 100
     }
}
```

#### Response
```
{
  "actionname": "get_transaction_list",
  "status": "get_transaction_list ok",
  "action_result": [
    {
      "id": "0",
      "timestamp": "10.05.2017 15:34:24",
      "from_user": "(user)",
      "to_user": "(user)",
      "bananacount": 1,
      "comment": "(kommentar)"
    },
    {
      "id": "1",
      "timestamp": "11.05.2017 12:34:24",
      "from_user": "(user)",
      "to_user": "(user)",
      "bananacount": 1,
      "comment": "(kommentar)"
    }
  ]
}
```

### CreateTransaction

Überschreibt eine Banane von einem User (identifiziert durch Token) an einen anderen (identifiziert durch display name)

| Eingabe | Ausgabe |
| --- | --- |
| Token, Ziel-User, Anzahl Bananen, Kommentar | Transaktions ID |

#### Request
```
{
  "actionname": "create_transaction",
  "login": {
      "token": "(token)",
      "source": "(app or website)"
  }
  "action_request": {
    "to_user": "(user_display_name)",
    "banana_count": 1,
    "comment": "(kommentar)"
  }
}
```

#### Response
```
{
  "actionname": "create_transaction",
  "status": "send bananas ok",
  "action_result": [
   1337
  ]
}
```

EDIT_USER

#### Request
```
{
  "actionname": "edit_user",
   "login": {
      "token": "(token)",
      "source": "(app or website or bot)"
  }
  "action_request": {
    "display_name": "Britney",
    "ad_user": "britney.spears",
    "is_admin": 0,
    "banana_to_spend": 10,
    "token_duration”: 168,
    "login_token”: “sda234ass234234adsad2342324da”,
    “token_expiration_timestamp” : “12.09.2017 12:12:12”
    "isNewUser": 1
  }
}
```

#### Response
```
{
  "actionname": "edit_user",
  "status": edit user ok",
  "action_result": [
   true
  ]
}
```

wenn isNewUser 0: nur display_name Pflicht, alles andere optional
wenn isNewUser 1: alle Felder Pflicht


### Logout

Invalidiert den Token

| Eingabe | Ausgabe |
| --- | --- |
| Token | true/false |

#### Request
```
{
  "actionname": "logout",
  "login": {
      "token": "(token)",
      "source": "(app or website)"
  }
}
```

#### Response
```
{
  "actionname": "logout",
  "status": logout ok",
  "action_result": [
  ]
}
```

BANANA_RAIN

#### Request
```
{
  "actionname": "banana_rain",
  "login": {
      "token": "(token)",
      "source": "(app or website)"
  }
}
```

#### Response
```
{
  "actionname": "logout",
  "status": banana_rain ok",
  "action_result": [
  ]
}
```


EDIT_TRANSACTION

#### Request
```
{
  "actionname": "edit_transaction",
   "login": {
      "token": "(token)",
      "source": "(app or website or bot)"
  }
  "action_request": {
    "transaction_id": "12345",
    "comment": "sdsdff sdf ssdfsdffds sdf sdfsdsdfsdf sd",
  }
}
```

#### Response
```
{
  "actionname": "edit_transaction",
  "status": edit_transaction ok",
  "action_result": [
   true
  ]
}
```
nicht fürs Löschen gedacht, was nur Admins können (neuer Call in Zukunft)
