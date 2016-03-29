/* JS functions to calculate the Living Conditions Score, based on a map provied as JSON file */

var calculateLCS = function (input, json) {

	var score = null;
	var lcs = {};	

	lcs.isComplete = "";
	lcs.value = "";
	lcs.error = "";

	/* Work the input to create object */

	var userInput = [];

	for (var individualInput in input) {

		var questionWithAnswer = {};
		questionWithAnswer.question = individualInput;
		questionWithAnswer.answer = input[individualInput];

		userInput.push(questionWithAnswer);
	}


	/* Compare input to provided map */

	// Iterate through each user input

	var count = 0;

	for (var entry in userInput) {

		var questionWithAnswer = userInput[entry];
		var hasConfig = false;

		for (var q in json.questions ) {
			
			var question = json.questions[q];
			var scoreList = question.scores;

			if (questionWithAnswer.question == question.conceptMapping) {
				
				count = count + 1;

				hasConfig = true;
				var questionScore;

				if (question.isNumeric == "true") {
					questionScore = getScoreFromNumeric(questionWithAnswer.answer, scoreList);
				} else {
					questionScore = getScoreFromConcept(questionWithAnswer.answer, scoreList);
				}

				if (questionScore) {
					score = questionScore *1 + score;
				} else {
					lcs.value = null;
					lcs.error="1";
					return lcs;
				}
			}
		}

		if (!hasConfig) {
			lcs.value = null;
			lcs.error="2";
			return lcs;
		}
	}

	lcs.value = score;
	lcs.error="0";

	if (json.questions.length == count) {
		lcs.isComplete = "true";
	} else {
		lcs.isComplete = "false";
	}

	return lcs;
}

var getScoreFromNumeric = function (userAnswer, answersScore) {

	var score = null;

	for (var currentScore in answersScore){

		var aS = answersScore[currentScore];
		var match = false;

		switch (aS.operator) {

			case "<=": 
			if (userAnswer <= aS.value*1) {
				match = true;
			}
			break;

			case ">=":
			if (userAnswer >= aS.value*1) {
				match = true;
			}
			break;

			case "<":
			if (userAnswer < aS.value*1) {
				match = true;
			}
			break;

			case ">":
			if (userAnswer > aS.value*1) {
				match = true;
			}
			break;

			case "=":
			if (userAnswer == aS.value*1) {
				match = true;
			}
			break;

			case undefined:
			if (userAnswer == aS.value*1) {
				match = true;
			}
			break;
		}

		if (aS.high && aS.low) {
			if (userAnswer >= aS.low*1 && userAnswer <= aS.high*1 ) {
				match = true;
			}

		}

		if (match) {
			score = currentScore;
		}
	}
	return score;
}

var getScoreFromConcept = function (userAnswer, answersScore) {
	var score = null;
	for (var currentScore in answersScore){
		var aS = answersScore[currentScore];

		if (userAnswer === aS.conceptId) {
			score = aS.score;
		}
	} 
	return score;
}
