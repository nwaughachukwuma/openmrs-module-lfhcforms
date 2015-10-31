SELECT
	CONVERT(myidentifier.identifier, char) AS 'Identifier',
	myperson.full_name AS 'Full name',
    DATE_FORMAT(myperson.birthdate,'%d-%m-%Y') AS 'DOB',
    @years := TIMESTAMPDIFF(YEAR, myperson.birthdate, visit.date_started) AS 'Years',
    @months := TIMESTAMPDIFF(MONTH, myperson.birthdate, visit.date_started) - 12 * TIMESTAMPDIFF(YEAR, myperson.birthdate, visit.date_started) AS 'Months',
    TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(myperson.birthdate, INTERVAL @years YEAR), INTERVAL @months MONTH), visit.date_started) AS 'Days',
	myperson.gender AS 'Gender',
    IFNULL(myperson.father_name,"") AS 'Father\'s name',
    IFNULL(myperson.mother_name,"") AS 'Mother\'s name',
	CONVERT(IFNULL(myperson.phone,""), char) AS 'Phone',
	IFNULL(myperson.city_village,"") AS 'Village',
	IFNULL(myperson.county_district,"") AS 'District',
	IFNULL(myperson.state_province,"") AS 'Province',
	diagnoses.diagnoses AS 'Diagnoses',
	"" AS 'Onset date',
    DATE_FORMAT(visit.date_started,'%d-%m-%Y') AS 'Visit start date'
FROM
(
	SELECT
		visit.visit_id AS visit_id,
		visit.patient_id AS patient_id,
		GROUP_CONCAT(DISTINCT visit.diagnosis SEPARATOR ', ') AS diagnoses
	FROM
	(
		SELECT
			myencounter.visit_id AS visit_id,
			myencounter.patient_id AS patient_id,
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
					concept_class.uuid = '8d4918b0-c2cc-11de-8d13-0010c6dffd0f'
			)
			AND myencounter.encounter_type_uuid = '3dbd13da-f210-4f20-a5b4-536a92e81474'
	)
	AS visit
	GROUP BY visit.visit_id
	ORDER BY visit.visit_id DESC
)
AS diagnoses
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
				myaddress.state_province
			FROM
				person
				LEFT JOIN
				(
					SELECT
						person_name.person_id,
						CONCAT(person_name.given_name, ' ', person_name.family_name) AS full_name
					FROM
						person_name
					WHERE
						person_name.date_created = (	SELECT
															MAX(nm2.date_created)
														FROM
															person_name nm2
														WHERE
															nm2.person_id = person_name.person_id 	)
				)
				AS myname
				ON
					person.person_id = myname.person_id				
				LEFT JOIN
                (
					SELECT
						person_attribute.person_id AS person_id,
                        person_attribute.value AS value
                    FROM
						person_attribute
						LEFT JOIN
							person_attribute_type
						ON
							person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
                            person_attribute_type.uuid = '14d4f066-15f5-102d-96e4-000c29c2a5d7'
					WHERE
						person_attribute.date_created =  (	SELECT
																MAX(pa2.date_created)
															FROM
																person_attribute pa2
															WHERE
																pa2.person_id = person_attribute.person_id 	AND
                                                                pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id)
				) AS phone
                ON
					person.person_id = phone.person_id
				LEFT JOIN
                (
					SELECT
						person_attribute.person_id AS person_id,
                        person_attribute.value AS value
                    FROM
						person_attribute
						LEFT JOIN
							person_attribute_type
						ON
							person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
                            person_attribute_type.uuid = '51c5e4f4-7e13-11e5-8bcf-feff819cdc9f'
					WHERE
						person_attribute.date_created =  (	SELECT
																MAX(pa2.date_created)
															FROM
																person_attribute pa2
															WHERE
																pa2.person_id = person_attribute.person_id 	AND
                                                                pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id)
				) AS father_name
                ON
					person.person_id = father_name.person_id
				LEFT JOIN
                (
					SELECT
						person_attribute.person_id AS person_id,
                        person_attribute.value AS value
                    FROM
						person_attribute
						LEFT JOIN
							person_attribute_type
						ON
							person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id AND
                            person_attribute_type.uuid = '8d871d18-c2cc-11de-8d13-0010c6dffd0f'
					WHERE
						person_attribute.date_created =  (	SELECT
																MAX(pa2.date_created)
															FROM
																person_attribute pa2
															WHERE
																pa2.person_id = person_attribute.person_id 	AND
                                                                pa2.person_attribute_type_id = person_attribute_type.person_attribute_type_id)
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
						person_address.date_created = (	SELECT
															MAX(pa2.date_created)
														FROM
															person_address pa2
														WHERE
															pa2.person_id = person_address.person_id 	)
				)
				AS myaddress
				ON
					person.person_id = myaddress.person_id
		)
		AS myperson
	ON
		diagnoses.patient_id = myperson.person_id
	LEFT JOIN
		(
			SELECT
				patient_identifier.patient_id,
                patient_identifier.identifier
		
			FROM
				patient_identifier
			WHERE
				patient_identifier.date_created = (	SELECT
														MAX(pi2.date_created)
													FROM
														patient_identifier pi2
													WHERE
														pi2.patient_id = patient_identifier.patient_id 	)
        )
        AS myidentifier
	ON
		diagnoses.patient_id = myidentifier.patient_id
	LEFT JOIN
		visit
	ON
		diagnoses.visit_id = visit.visit_id
/*WHERE
    visit.date_started >= :startDate AND
    visit.date_started <= :endDate*/
ORDER BY visit.date_started DESC
;