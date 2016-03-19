/* JS functions to calculate the Living Conditions Score, based on a map provied as JSON file */

// TODO: Retrieve this map from external resource
var json = {
	"name":"lcsCalculationTable",
	"description":"Holds a list of each question that is taken into account while calucation the Living Condition score. Each question has then a scoreMap property that maps the possible answers with their associated score",
	"questions": {
		"LFHC:1000":{
			"scoreMap": {
				"0": {
					"value":"7",
					"operator":">="
				},
				"1":{
					"low": "2",
					"high":"6"
				},
				"2": {
					"value":"1"
				},
				"3": {
					"value": "0"
				}
			}
		},
		"LFHC:1001":{
			"scoreMap": {
				"0":"LFHC:1100",
				"1":"LFHC:1101",
				"2":"LFHC:1102"
			}
		}
	}
};

var input = {
	"LFHC:1000":"3",
	"LFHC:1001":"LFHC:1102",
}


var calculateLCS = function (input, jsonMap) {

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

		for (var question in jsonMap.questions ) {
			
			var scoreMap = jsonMap.questions[question].scoreMap;

			if (questionWithAnswer.question == question) {
				hasConfig = true;
				var questionScore;
				if (questionWithAnswer.isNumeric) {
					questionScore = getScoreFromNumeric(questionWithAnswer.answer, scoreMap);
				} else {
					questionScore = getScoreFromConcept(questionWithAnswer.answer, scoreMap);
				}
				// console.log(question + ": " + questionScore);
				if (questionScore) {
					score = questionScore *1 + score;
				} else {
					// console.warn("Score value for answer " + questionWithAnswer.answer + " (question " + question + ") is not found in configuration file")
					lcs.value = null;
					lcs.error="1";
					return lcs;
				}
			}
		}

		if (!hasConfig) {
			// console.warn("question " + questionWithAnswer.question + " is not found in configuration file")
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
	//// console.log('is numeric');

	var score = null;

	for (var currentScore in answersScore){

		var aS = answersScore[currentScore];
		var match = false;
		// console.log(aS);

		switch (aS.operator) {

			case "<=": 
			if (userAnswer <= aS.value*1) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + aS.operator + aS.value + ")");
				match = true;
			}
			break;

			case ">=":
			if (userAnswer >= aS.value*1) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + aS.operator + aS.value + ")");
				match = true;
			}
			break;

			case "<":
			if (userAnswer < aS.value*1) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + aS.operator + aS.value + ")");
				match = true;
			}
			break;

			case ">":
			if (userAnswer > aS.value*1) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + aS.operator + aS.value + ")");
				match = true;
			}
			break;

			default:
			if (userAnswer == aS.value*1) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + "=" + aS.value + ")");
				match = true;
			}

		}

		if (aS.high && aS.low) {
			if (userAnswer >= aS.low && userAnswer <= aS.high ) {
				// console.log("Score: "+ currentScore + " (" +userAnswer + ' in [' + aS.low +"-"+ aS.high + "])");
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
	// console.log('is concept');
	var score = null;
	for (var currentScore in answersScore){
		// console.log(answersScore[currentScore]);
		
		if (userAnswer === answersScore[currentScore]) {
			score = currentScore;
		}
	} 
	return score;
}


