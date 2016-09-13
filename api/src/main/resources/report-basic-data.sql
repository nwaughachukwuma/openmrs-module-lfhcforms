SELECT
  DATE_FORMAT(myregistration.date,'%d-%m-%Y') AS 'Registration date',
  CONVERT(omrs_identifier.identifier, char) AS 'Identifier',
  myperson.full_name AS 'Full name',
  @years := TIMESTAMPDIFF(YEAR, myperson.birthdate, visit.date_started) AS 'Years',
  @months := TIMESTAMPDIFF(MONTH, myperson.birthdate, visit.date_started) - 12 * TIMESTAMPDIFF(YEAR, myperson.birthdate, visit.date_started) AS 'Months',
  TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(myperson.birthdate, INTERVAL @years YEAR), INTERVAL @months MONTH), visit.date_started) AS 'Days',
  myperson.gender AS 'Gender',
  IFNULL(myperson.father_name, "") AS 'Father\'s name',
  IFNULL(myperson.mother_name, "") AS 'Mother\'s name',
  CONVERT(IFNULL(myperson.phone, ""), char) AS 'Phone',
  IFNULL(myperson.city_village, "") AS 'Village',
  IFNULL(myperson.county_district, "") AS 'District',
  IFNULL(myperson.state_province, "") AS 'Province',
  IFNULL(myperson.ethnicity, "") AS 'Ethnicity',
  complaints_diagnoses.complaints AS 'Presenting complaints',
  complaints_diagnoses.diagnoses AS 'Diagnoses',
  IFNULL(illness_days.number, "") AS 'Days sick',
  visit_type.name AS 'Visit type',
  IFNULL(discharge_conditions.discharge_conditions, "") AS 'Condition at discharge',
  DATE_FORMAT(visit.date_started,'%d-%m-%Y') AS 'Visit start date',
  IFNULL(DATE_FORMAT(visit.date_stopped,'%d-%m-%Y'), "") AS 'Visit end date'
FROM
(

  --
  -- Below the UNION of a LEFT OUTER JOIN and a RIGHT OUTER JOIN mapping complaints to diagnoses
  --

  SELECT
    IFNULL(complaints.visit_id, diagnoses.visit_id) AS visit_id,
    IFNULL(complaints.patient_id, diagnoses.patient_id) AS patient_id,
    IFNULL(complaints.complaints, "") AS complaints,
    IFNULL(diagnoses.diagnoses, "") AS diagnoses
  FROM
      (
        SELECT
          visit.visit_id AS visit_id,
          visit.patient_id AS patient_id,
          GROUP_CONCAT(DISTINCT visit.complaint SEPARATOR ', ') AS complaints
        FROM
        (
          SELECT
            myencounter.visit_id AS visit_id,
            myencounter.patient_id AS patient_id,
            myconcept.concept_name AS complaint
          FROM
            obs
            LEFT JOIN
              (
                SELECT
                  encounter.encounter_id,
                  encounter.visit_id,
                  encounter.patient_id
                FROM encounter
              )
              AS myencounter
            ON
              obs.encounter_id = myencounter.encounter_id
            LEFT JOIN
              (
                SELECT
                  concept.concept_id,
                  concept_name.name AS concept_name
                FROM concept
                  LEFT JOIN concept_name
                  ON
                    concept_name.concept_id = concept.concept_id AND
                    concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
                    concept_name.locale = 'en'
              )
              AS myconcept
            ON
              obs.value_coded = myconcept.concept_id
          WHERE obs.concept_id
                = ( SELECT concept.concept_id
                    FROM concept
                    WHERE concept.uuid = '5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' ) -- Chief complaint, CIEL:5219
        )
        AS visit
        GROUP BY visit.visit_id, visit.patient_id
      ) AS complaints
      LEFT OUTER JOIN
        (
          SELECT
            visit.visit_id AS visit_id,
            visit.patient_id AS patient_id,
            GROUP_CONCAT(DISTINCT visit.diagnosis SEPARATOR ', ') AS diagnoses
          FROM
          (
            SELECT
              myencounter.visit_id,
              myencounter.patient_id,
              myconcept.concept_name AS diagnosis
            FROM obs
            LEFT JOIN
              (
                SELECT
                  encounter.encounter_id AS encounter_id,
                  encounter_type.encounter_type_id AS encounter_type_id,
                  encounter_type.uuid AS encounter_type_uuid,
                  encounter.visit_id AS visit_id,
                  encounter.patient_id AS patient_id
                FROM encounter
                  LEFT JOIN encounter_type
                  ON
                    encounter.encounter_type = encounter_type.encounter_type_id
              )
              AS myencounter
              ON
                obs.encounter_id = myencounter.encounter_id
              LEFT JOIN
                (
                  SELECT
                    concept.concept_id AS concept_id,
                    concept_name.name AS concept_name
                  FROM concept
                    LEFT JOIN concept_name
                    ON
                      concept_name.concept_id = concept.concept_id AND
                      concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
                      concept_name.locale = 'en'
                )
                AS myconcept
              ON
                obs.value_coded = myconcept.concept_id
            WHERE obs.value_coded IN
              (
                SELECT concept.concept_id
                FROM concept
                  LEFT JOIN concept_class
                  ON
                    concept.class_id = concept_class.concept_class_id
                WHERE 
                  concept_class.uuid = '8d4918b0-c2cc-11de-8d13-0010c6dffd0f' -- Diagnosis concept class
              )
              AND myencounter.encounter_type_uuid = '3dbd13da-f210-4f20-a5b4-536a92e81474' -- Diagnosis encounter type
          )
          AS visit
          GROUP BY visit.visit_id
        )
        AS diagnoses
      ON
        complaints.visit_id = diagnoses.visit_id AND
        complaints.patient_id = diagnoses.patient_id
        
      UNION

      SELECT
        IFNULL(complaints.visit_id, diagnoses.visit_id) AS visit_id,
        IFNULL(complaints.patient_id, diagnoses.patient_id) AS patient_id,
        IFNULL(complaints.complaints, "") AS complaints,
        IFNULL(diagnoses.diagnoses, "") AS diagnoses
      FROM
      (
        SELECT
          visit.visit_id AS visit_id,
          visit.patient_id AS patient_id,
          GROUP_CONCAT(DISTINCT visit.complaint SEPARATOR ', ') AS complaints
        FROM
        (
          SELECT
            myencounter.visit_id AS visit_id,
            myencounter.patient_id AS patient_id,
            myconcept.concept_name AS complaint
          FROM
            obs
            LEFT JOIN
              (
                SELECT
                  encounter.encounter_id,
                  encounter.visit_id,
                  encounter.patient_id
                FROM encounter
              ) AS myencounter
            ON obs.encounter_id = myencounter.encounter_id
            LEFT JOIN
              (
                SELECT
                  concept.concept_id,
                  concept_name.name AS concept_name
                FROM concept
                  LEFT JOIN concept_name
                  ON
                    concept_name.concept_id = concept.concept_id AND
                    concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
                    concept_name.locale = 'en'
              ) AS myconcept
            ON
              obs.value_coded = myconcept.concept_id
          WHERE obs.concept_id
                = ( SELECT concept.concept_id
                    FROM concept
                    WHERE concept.uuid = '5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' ) -- Chief complaint, CIEL:5219
        ) AS visit
        GROUP BY visit.visit_id, visit.patient_id
      ) AS complaints
      RIGHT OUTER JOIN
        (
          SELECT
            visit.visit_id AS visit_id,
            visit.patient_id AS patient_id,
            GROUP_CONCAT(DISTINCT visit.diagnosis SEPARATOR ', ') AS diagnoses
          FROM
          (
            SELECT
              myencounter.visit_id,
              myencounter.patient_id,
              myconcept.concept_name AS diagnosis
            FROM
              obs
              LEFT JOIN
                (
                  SELECT
                    encounter.encounter_id AS encounter_id,
                    encounter_type.encounter_type_id AS encounter_type_id,
                    encounter_type.uuid AS encounter_type_uuid,
                    encounter.visit_id AS visit_id,
                    encounter.patient_id AS patient_id
                  FROM encounter
                    LEFT JOIN encounter_type
                    ON encounter.encounter_type = encounter_type.encounter_type_id
                ) AS myencounter
              ON
                obs.encounter_id = myencounter.encounter_id
              LEFT JOIN
                (
                  SELECT
                    concept.concept_id AS concept_id,
                    concept_name.name AS concept_name
                  FROM concept
                    LEFT JOIN concept_name
                    ON
                      concept_name.concept_id = concept.concept_id AND
                      concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
                      concept_name.locale = 'en'
                ) AS myconcept
              ON obs.value_coded = myconcept.concept_id
            WHERE obs.value_coded IN
              (
                SELECT concept.concept_id
                FROM concept
                  LEFT JOIN concept_class
                  ON concept.class_id = concept_class.concept_class_id
                WHERE 
                  concept_class.uuid = '8d4918b0-c2cc-11de-8d13-0010c6dffd0f'
              )
              AND myencounter.encounter_type_uuid = '3dbd13da-f210-4f20-a5b4-536a92e81474'
          )
          AS visit
          GROUP BY visit.visit_id
        )
        AS diagnoses
      ON
        complaints.visit_id = diagnoses.visit_id AND
        complaints.patient_id = diagnoses.patient_id
      WHERE
        complaints.visit_id IS NULL
  ORDER BY patient_id ASC, visit_id ASC
)
AS complaints_diagnoses

  --
  -- Below a stream of LEFT JOINs to complement 'complaints_diagnoses'
  --

  LEFT JOIN
    (visit AS visits, visit_type)
  ON
    complaints_diagnoses.visit_id = visits.visit_id AND
    visit_type.visit_type_id = visits.visit_type_id

  LEFT JOIN
    (
      SELECT
        visit.visit_id AS visit_id,
        GROUP_CONCAT(DISTINCT visit.discharge_condition SEPARATOR ', ') AS discharge_conditions
      FROM
        (
          SELECT
            myencounter.visit_id AS visit_id,
            myconcept.concept_name AS discharge_condition
          FROM obs
          LEFT JOIN
          (
            SELECT
              encounter.encounter_id,
              encounter.visit_id,
              encounter.patient_id
            FROM
              encounter
          ) AS myencounter
          ON
            obs.encounter_id = myencounter.encounter_id
          LEFT JOIN
          (
            SELECT
              concept.concept_id,
              concept_name.name AS concept_name
            FROM concept
            LEFT JOIN concept_name
            ON
              concept_name.concept_id = concept.concept_id AND
              concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
              concept_name.locale = 'en'
            )
          AS myconcept
          ON
            obs.value_coded = myconcept.concept_id
          WHERE obs.concept_id
          = ( SELECT concept.concept_id
              FROM concept
              WHERE concept.uuid = 'ebac4918-745a-45d9-be02-44badfa45fde' ) -- LFHC:1101 Condition at Discharge
        ) AS visit
      GROUP BY visit.visit_id
    )
  AS discharge_conditions
  ON
    discharge_conditions.visit_id = visits.visit_id
  
  LEFT JOIN
    (
      SELECT
        person.person_id AS person_id,
        person.birthdate AS birthdate,
        person.gender AS gender,
        myname.full_name,
        phone.value AS phone,
        father_name.value AS father_name,
        mother_name.value AS mother_name,
        myaddress.city_village,
        myaddress.county_district,
        myaddress.state_province,
        myethnicity.name AS ethnicity
      FROM
        person
        LEFT JOIN
        (
          SELECT
            person_name.person_id,
            CONCAT(person_name.given_name, ' ', person_name.family_name) AS full_name
          FROM person_name
          WHERE
            person_name.date_created
            = ( SELECT MAX(nm2.date_created) FROM person_name nm2
                WHERE nm2.person_id = person_name.person_id )
        )
        AS myname
        ON
          person.person_id = myname.person_id       
        LEFT JOIN
        (
          SELECT
            person_attribute.person_id AS person_id,
            person_attribute.value AS value
          FROM person_attribute
          LEFT JOIN person_attribute_type
          ON
            person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
            person_attribute_type.uuid = '14d4f066-15f5-102d-96e4-000c29c2a5d7'
          WHERE
            person_attribute.date_created
            = ( SELECT MAX(pa2.date_created)
                FROM person_attribute pa2
                WHERE
                  pa2.person_id = person_attribute.person_id  AND
                  pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id )
        ) AS phone
        ON person.person_id = phone.person_id
        LEFT JOIN
        (
          SELECT
            person_attribute.person_id AS person_id,
            person_attribute.value AS value
          FROM person_attribute
          LEFT JOIN person_attribute_type
          ON
            person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
            person_attribute_type.uuid = '51c5e4f4-7e13-11e5-8bcf-feff819cdc9f'
          WHERE
            person_attribute.date_created
            = ( SELECT MAX(pa2.date_created)
                FROM person_attribute pa2
                WHERE
                  pa2.person_id = person_attribute.person_id  AND
                  pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id )
        ) AS father_name
        ON
          person.person_id = father_name.person_id
        LEFT JOIN
        (
          SELECT
            person_attribute.person_id AS person_id,
            person_attribute.value AS value
          FROM person_attribute
          LEFT JOIN person_attribute_type
          ON
            person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
            person_attribute_type.uuid = '8d871d18-c2cc-11de-8d13-0010c6dffd0f'
          WHERE
            person_attribute.date_created
            = ( SELECT MAX(pa2.date_created)
                FROM person_attribute pa2
                WHERE
                  pa2.person_id = person_attribute.person_id  AND
                  pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id )
        ) AS mother_name
        ON
          person.person_id = mother_name.person_id
        LEFT JOIN
        (
          SELECT
            person_address.person_id,
            person_address.city_village,
            person_address.county_district,
            person_address.state_province
          FROM
            person_address
          WHERE
            person_address.date_created
            = ( SELECT MAX(pa2.date_created)
                FROM person_address pa2
                WHERE pa2.person_id = person_address.person_id  )
        )
        AS myaddress
        ON
          person.person_id = myaddress.person_id
        LEFT JOIN
        (
          SELECT
            obs.person_id,
            concept_name.name     
          FROM
            obs
          LEFT JOIN
            concept_name
          ON
            obs.value_coded = concept_name.concept_id
          WHERE
            concept_name.concept_name_type = 'FULLY_SPECIFIED' AND
            concept_name.locale = 'en' AND
            obs.obs_datetime
            =  (  SELECT MAX(ob2.obs_datetime)
                  FROM obs ob2
                  WHERE
                    ob2.person_id = obs.person_id AND
                    ob2.concept_id = obs.concept_id )
            AND
            obs.concept_id
            = (
                SELECT DISTINCT
                  concept_reference_map.concept_id
                FROM
                  concept_reference_map
                LEFT JOIN
                  (concept, concept_name, concept_reference_term, concept_map_type, concept_reference_source)
                ON
                  concept_reference_map.concept_id = concept.concept_id AND
                  concept_reference_map.concept_id = concept_name.concept_id AND
                  concept_reference_map.concept_map_type_id = concept_map_type.concept_map_type_id AND
                  concept_reference_map.concept_reference_term_id = concept_reference_term.concept_reference_term_id AND
                  concept_reference_term.concept_source_id = concept_reference_source.concept_source_id
                WHERE
                  concept_reference_source.name = "LFHC" AND concept_reference_term.code = 912
              )
        )
        AS myethnicity
        ON
          person.person_id = myethnicity.person_id
    )
    AS myperson
  ON
    complaints_diagnoses.patient_id = myperson.person_id

  LEFT JOIN
    (
      SELECT
        patient_identifier.patient_id,
        patient_identifier.identifier
      FROM
        patient_identifier
      LEFT JOIN
        patient_identifier_type
      ON
        patient_identifier.identifier_type = patient_identifier_type.patient_identifier_type_id
      WHERE
        patient_identifier.date_created
        = ( SELECT MAX(pi2.date_created)
            FROM patient_identifier pi2
            LEFT JOIN patient_identifier_type pit2
            ON
              pi2.identifier_type = pit2.patient_identifier_type_id
            WHERE
              pi2.patient_id = patient_identifier.patient_id  
              AND pit2.uuid = '05a29f94-c0ed-11e2-94be-8c13b969e334'  )
        AND patient_identifier_type.uuid = '05a29f94-c0ed-11e2-94be-8c13b969e334' -- OpenMRS identifier type
    ) AS omrs_identifier
  ON
    complaints_diagnoses.patient_id = omrs_identifier.patient_id

  LEFT JOIN
    visit
  ON
    complaints_diagnoses.visit_id = visit.visit_id

  LEFT JOIN
    (
      SELECT
        encounter.patient_id AS patient_id,
        encounter.encounter_datetime AS date
      FROM
        encounter
      LEFT JOIN
        encounter_type
      ON
        encounter.encounter_type = encounter_type.encounter_type_id
      WHERE
        encounter_type.uuid = '3e3424bd-6e9d-4c9c-b3a4-f3fee751fe7c' AND -- registration encounter type's UUID
        encounter.date_created
        = ( SELECT MIN(en2.date_created) -- We retain the oldest registration encounter in case of merged patients.
            FROM encounter en2
            WHERE en2.patient_id = encounter.patient_id   )
    ) AS myregistration
  ON
    complaints_diagnoses.patient_id = myregistration.patient_id
  
  LEFT JOIN
    (
      SELECT
        dat.visit_id,
        dat.patient_id,
        SUBSTRING_INDEX(GROUP_CONCAT(days SEPARATOR '|'), '|', 1) AS number
      FROM
      (
        SELECT
          encounter.visit_id,
          encounter.patient_id,
          obs.value_numeric days
        FROM
          obs
        LEFT JOIN
          (encounter, encounter_type, concept)
        ON
          encounter.encounter_id = obs.encounter_id
          AND obs.concept_id = concept.concept_id
          AND encounter_type.encounter_type_id = encounter.encounter_type
        WHERE
          concept.uuid = '1553AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' -- CIEL:1553 "Duration of illness"
        ORDER BY encounter.visit_id ASC, encounter.patient_id ASC, encounter_type.uuid ASC, obs.obs_datetime DESC, obs.date_created DESC
        -- Doctor History's UUID = a9702711, OPD Nurse's UUID = c7700650
      )
      AS dat
      GROUP BY dat.visit_id, dat.patient_id
    ) AS illness_days
  ON
    complaints_diagnoses.patient_id = illness_days.patient_id AND
    complaints_diagnoses.visit_id = illness_days.visit_id

WHERE
  visit.date_started >= :startDate AND
  visit.date_started < DATE_ADD(:endDate, INTERVAL 1 DAY) -- Because :endDate is set at 00:00
ORDER BY visit.date_started DESC, omrs_identifier.identifier ASC
;