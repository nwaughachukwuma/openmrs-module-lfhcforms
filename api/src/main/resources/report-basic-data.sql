SELECT
	CONVERT(patient_identifier.identifier, char) AS 'Identifier',
	myperson.full_name AS 'Full name',
    IF(myperson.ageYears > 0, CONCAT(myperson.ageYears, 'y'), CONCAT(myperson.ageMonths, 'm')) as 'Age',
	myperson.gender AS 'Gender',
	"" AS 'Parent name',
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
                TIMESTAMPDIFF(YEAR, person.birthdate, CURDATE()) AS ageYears,
                TIMESTAMPDIFF(MONTH, person.birthdate, CURDATE()) AS ageMonths,
				person.gender AS gender,
				CONCAT(person_name.given_name, ' ', person_name.family_name) AS full_name,
				person_attribute.value AS phone,
				myaddress.city_village,
				myaddress.county_district,
				myaddress.state_province
			FROM
				person
				LEFT JOIN
					person_name
				ON
					person.person_id = person_name.person_id
				LEFT JOIN
					person_attribute
				ON
					(person.person_id = person_attribute.person_id AND person_attribute.person_attribute_type_id = '8')
				LEFT JOIN
				(
					SELECT
						ad1.person_id,
						ad1.city_village,
						ad1.county_district,
						ad1.state_province
					FROM
						person_address ad1
					WHERE
						ad1.date_created = (	SELECT
													MAX(ad2.date_created)
												FROM
													person_address ad2
												WHERE
													ad2.person_id = ad1.person_id 	)
				)
				AS myaddress
				ON
					person.person_id = myaddress.person_id
		)
		AS myperson
	ON
		diagnoses.patient_id = myperson.person_id
	LEFT JOIN
		patient_identifier
	ON
		diagnoses.patient_id = patient_identifier.patient_id
	LEFT JOIN
		visit
	ON
		diagnoses.visit_id = visit.visit_id
WHERE
    visit.date_started >= :startDate AND
    visit.date_started <= :endDate
ORDER BY visit.date_started DESC
;