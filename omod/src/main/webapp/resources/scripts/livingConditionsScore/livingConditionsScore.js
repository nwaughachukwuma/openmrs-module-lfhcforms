/* JS functions to calculate the Living Conditions Score, based on a map provied as JSON file */

var calculateLCS = function (input, json) {

	var score = null;
	var lcs = {};	

	/* Work the input to create object */

	var userInput = [];

	for (var individualInput in input) {

		var questionWithAnswer = {};
		questionWithAnswer.question = individualInput;
		questionWithAnswer.answer = input[individualInput];

		if ((questionWithAnswer.answer.indexOf(":")) > 0) {
			questionWithAnswer.isNumeric = false;
		} else {
			questionWithAnswer.isNumeric = true;
		}

		userInput.push(questionWithAnswer);
	}


	/* Compare input to provided map */

	for (var input in userInput) {

		var questionWithAnswer = userInput[input];
		var hasConfig = false;

		for (var q in json.questions ) {
			
			var question = json.questions[q];
			var scoreList = question.scores;

			if (questionWithAnswer.question == question.conceptMapping) {
				
				hasConfig = true;
				var questionScore;

				if (questionWithAnswer.isNumeric) {
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
			if (userAnswer >= aS.low && userAnswer <= aS.high ) {
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

		if (userAnswer === aS.conceptMapping) {
			score = currentScore;
		}
	} 
	return score;
}
