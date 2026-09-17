# PlaceTrack — Postman Collection

This folder contains a ready-to-import Postman collection for the **existing** PlaceTrack Spring Boot backend. Every request was written by inspecting the actual controllers, DTOs and entities in `backend/src/main/java/com/placetrack/backend/` — no invented endpoints or fields.

| File | Description |
|---|---|
| `PlaceTrack.postman_collection.json` | 13 folders, 58 requests, with example bodies and test scripts |
| `README.md` | This guide |

---

## 1. Import into Postman

1. Open **Postman** (desktop app or web).
2. Click **Import** (top-left).
3. Choose **File** and select `postman/PlaceTrack.postman_collection.json`.
4. Click **Import**. The collection **PlaceTrack API** appears with 13 folders:
   `Authentication`, `Students`, `Companies`, `Placement Drives`, `Job Opportunities`, `Placements`, `Profile`, `Skills`, `Projects`, `Certifications`, `Internships`, `Resume`, `Dashboard`.

## 2. Start the PlaceTrack backend

From the project root (Java 17+ and MySQL must be available):

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Or with Maven installed globally:

```powershell
cd backend
mvn spring-boot:run
```

Wait for `Started PlaceTrackApplication` in the console. The API is then served at `http://localhost:8080`. (Seed data such as demo companies/jobs is inserted automatically at startup if configured.)

## 3. Set `{{baseUrl}}`

- The collection already defines `baseUrl = http://localhost:8080` as a **collection variable**.
- To change it: click the collection → **Variables** tab → edit `baseUrl` → **Save**.
- All 58 requests use `{{baseUrl}}`, so a single edit re-points the whole collection (e.g. to a deployed host).

## 4. Authentication / token handling

PlaceTrack does **not** use Bearer/JWT. It uses a **server-generated session token sent in the `X-Auth-Token` header**.

How it works in this collection:

1. Run **Authentication → Register** (first time) or **Authentication → Login**.
2. Both return a JSON body containing a `token` field; the built-in test script stores it automatically:
   ```js
   pm.collectionVariables.set("token", pm.response.json().token);
   ```
3. Every protected request already includes the header `X-Auth-Token: {{token}}`, so nothing else is needed.
4. Tokens expire after 24 hours — just run Login again.
5. To verify protection, clear the `token` collection variable (Collection → Variables → reset) and send any protected request: the backend answers **401 Unauthorized**.

Public (no token needed): `/api/auth/register`, `/api/auth/login`, and the Students / Companies / Jobs / Placements / Dashboard CRUD endpoints. Token-protected: `/api/auth/logout`, all `/api/profile/**` endpoints (Profile, Skills, Projects, Certifications, Internships, Resume) and all `/api/placement-drives/**` endpoints.

## 5. Running the requests

Run requests top-to-bottom, folder by folder, because some depend on data created earlier:

1. **Authentication → Register** (or **Login**) — sets `{{token}}`.
2. **Students → Create Student** — creates the student used later by Placements.
3. **Companies → Create Company** — creates the company used later by Jobs/Placements.
4. **Job Opportunities → Create Job** — needed by Placement Drives (`jobOpportunityId`).
5. **Placement Drives → Create Drive** — uses company + job ids.
6. **Placements → Create Placement** — uses student + company ids.
7. **Profile / Skills / Projects / Certifications / Internships / Resume** — operate on the logged-in user's own data.
8. **Dashboard** — read-only statistics.

Every request carries small test scripts (status-code and response-field checks) whose results appear in Postman's **Test Results** panel.

## 6. Testing CRUD operations

- **Create** → `Create Student/Company/Job/Placement` → expect **201 Created** with the saved entity (including its `id`).
- **Read** → `Get All …` (expect 200 + array) and `Get … By ID` (expect 200 + object; the sample URLs use id `1` — replace with a real id).
- **Update** → `Update …` → expect **200 OK** with the modified fields echoed back.
- **Delete** → `Delete …` → expect **200 OK** with a `message` body, or **204 No Content** for drives, skills, projects, certifications and internships. After deleting, send the matching `Get … By ID` again — it should now return **404 Not Found**.

## 7. Testing invalid / missing data (validation errors → 400)

Send a request with required fields removed or with bad values; the backend returns **400 Bad Request** with a JSON `ErrorResponse` containing a `fieldErrors` map.

- **Students**: omit `studentName`/`email`/`department`, set `year` to `0`, or an invalid `email`.
- **Placements**: omit `student`, `company`, `jobRole`, `packageLpa` or `placementDate`, or set `packageLpa` to a negative number.
- **Profile Update**: use a phone number that is not a 10-digit Indian mobile (e.g. `123`), or omit `studentName`.
- **Jobs**: omit `companyId` or `jobRole`, set `minCgpa` to `12.0`, or set `jobType`/`status` to an unsupported value.

## 8. Testing invalid IDs (→ 404)

Replace the id in any `…/{id}` URL with a value that does not exist (e.g. `999999`):

- `GET /api/students/999999`, `GET /api/companies/999999`, `GET /api/jobs/999999`, `GET /api/placements/999999`, `GET /api/placement-drives/999999`
- `GET /api/profile/skills/999999` and the other profile sub-resource `PUT`/`DELETE` routes
- `DELETE /api/students/999999`, `DELETE /api/companies/999999`

Each should return **404 Not Found** with the `ErrorResponse` body. Bonus: after a successful delete, the same `GET … By ID` returns 404 too.

## 9. Testing duplicate data (→ 409 / 400)

- **Duplicate student email**: run `Students → Create Student` twice with the same body. The second call returns **409 Conflict** (`A student with email '…' already exists`). The same check applies on `Update Student` if you change the email to one that already exists.
- **Duplicate registration**: run `Authentication → Register` twice with the same `email` → the second returns **409 Conflict**.
- **Protected student delete**: create a placement for a student, then call `Students → Delete Student` for that student — deletion is refused with **400 Bad Request** until its placements are deleted first.

---

## Notes

- Sample ids (`1`) in URLs are placeholders — always replace them with ids returned by the Create calls (or fetch a list first with the `Get All …` requests).
- Resume **Upload** is `multipart/form-data` with the file part named `file` (PDF/DOC/DOCX, max 5 MB). In Postman, open the request → **Body** → select the file. Use **Get Profile** to find the resume ids for **Download**/**Delete**.
- Search/filter parameters that exist per module:
  - Students: `search`, `department`, `status` (`PLACED` / `NOT_PLACED`)
  - Companies: `search`
  - Jobs: `search`, `status` (`OPEN` / `CLOSED` / `CANCELLED` / `COMPLETED`)
  - Placements: `status` (`SELECTED` / `REJECTED` / `PENDING`), `companyId`
  - Placement Drives: `upcoming=true`, plus `/by-company/{companyId}`
- No credentials, passwords, database settings or API keys are stored in this collection — `token` is populated at runtime by the login test script.

