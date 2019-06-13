# REST API 

Εδώ περιγράφεται το REST API που προσφέρει το back-end. Το input είναι URL-encoded και το output είναι σε JSon. Όπου αναφέρεται ότι το output είναι ένα object μιας κλάσης (πχ User object ή Room object) εννοούμε ότι είναι η JSon μορφή αυτού του αντικειμένου με όλα τα πεδία που έχει στην αντίστοιχη κλάση του backend. Επίσης, σε κάθε API call που απαιτεί authorisation θα πρέπει να υπάρχει στο HTTP Header τιμή με όνομα "token" και value το Jason Web Token (JWT) που πάρθηκε κατά το login.

Συμβάσεις:

| Συμβολισμός | Σημασία |
| --- | --- |
| name: JSon_output | το name είναι το όνομα του πεδίου του συνολικού JSon Object που επιστρέφεται με το JSon_output ως τιμή
| ∈ { ... }  | για να δείξουμε το πεδίο τιμών μιας παραμέτρου |
| [ ... ] ή [ ... : συνθήκη ]  |  για να δηλώσουμε προαιρετικές παραμέτρους ή παραμέτρους υπό συνθήκη |
| Dates | Dates should be in RFC Format yyyy-MM-dd |


# REST API End-Points

#### /login
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| POST   | email, password | token:login token on success and user: User object (or error) | performs authorization check and returns login token and user object on success

#### /users
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | -     | users: JSon array of all User Objects | returns all users |
| GET    | id    | user: User object with given id (if it exists) | returns user with given id (if it exists) |
| GET    | email | user: User object with given email (if it exists) | returns user with given email (if it exists) |
| GET    | emailPrefix | users: JSon array of all User Objects with given prefix on their emails | returns user with given prefix on their emails |
| GET    | role ∈ {"visitor", "provider", "admin"} | users: JSon array of all User Objects with given role | returns all users for given role
| POST   | email, password, password1, type ∈ {"visitor", "provider"}, [name, surname : if visitor], [providername : if provider], [autologin] | token: login token and user: User object if autologin given, otherwise success/error message | registers a new user
| PUT    | [email], [name, surname : if visitor], [providername : if provider] | success/error message | updates account info for user (password should reset seperatelly)

#### /admin
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| POST   | id, option ∈ {"ban", "unban", "delete", "promote"} | success/error message | bans/unbans/deletes/promotes a user with the given id

#### /book
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| POST   | userId, roomId, startDate, endDate | success/error message | books given room for given user on given time frame if available

#### /rooms
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | roomId | room: Room object | returns requested room
| POST   | providerId, price, capacity, cordX, cordY, cityName, roomName, [description, wifi, pool, shauna] | room: Room object on success or error | submits a new room to the system for given provider

#### /favourite_rooms
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | visitorId | favourite_rooms: JSon array of Rooms | returns the favourite rooms of given visitor
| POST   | visitorId, roomId | success/error message | adds given room to given visitor's list of favourite rooms
| DELETE | visitorId, roomId | success/error message | removes given room to given visitor's list of favourite rooms

#### /ratings
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | roomId | ratings: JSon array of Rating objects | returns ratings for given room
| POST   | visitorId, roomId, stars ∈ { 0, ..., 5 }, comment | success/error message | submit a rating
| DELETE | ratingId | success/error message | deletes given rating (must be admin)

#### /autocomplete
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | str   | cityNames: JSon array of city names that match str as a prefix | returns list of autocompleted city names

#### /search (TODO: add more parameters?)
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |
| GET    | [minPrice, maxPrice, maxDist, pointX, pointY, cityName, hasWifi, hasPool, hasShauna, people] | results: JSon array of eligible Rooms | returns eligible rooms based on search constraints given


#### /img (TODO)
| Method | Input | Output | Action |
| ------ | ----- | ------ | ------ |

