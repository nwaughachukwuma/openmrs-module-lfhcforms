describe("Living Conditions Score Calculation", function() {

	var json;

	beforeEach(function () {
		json = {
			"name":"lcsCalculationTable",
			"description":"Holds a list of each question that is taken into account when calculating the Living Condition score. Each question has a scoreMap property that maps the possible answers to it with their associated score",
			"questions": {
				"LFHC:1000":{
					"score": {
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
					"score": {
						"0":"LFHC:1100",
						"1":"LFHC:1101",
						"2":"LFHC:1102"
					}
				}
			}
		};	
	});


	it("should do basic score calculation", function() {
		var input = {
			"LFHC:1000":"1",
			"LFHC:1001":"LFHC:1101"
		}

		var score = calculateLCS(input,json);

		expect(score.value).toBe(3);
	});

	it("should handle range ('high' and 'low' properties)", function() {
		var input = {
			"LFHC:1000":"5"
		}

		var score = calculateLCS(input,json);
		expect(score.value).toBe(1);
	});

	it("should handle 'greater than' and 'lower than' operators", function() {
		var input = {
			"LFHC:1000":"10",
			"LFHC:1000":"0"
		}

		var score = calculateLCS(input,json);
		expect(score.value).toBe(3);
	});


	it("should return error code 1 when answer is not found", function() {
		var input = {
			"LFHC:1001":"LFHC:9999"
		}

		var score = calculateLCS(input,json);
		expect(score.error).toBe('1');
		expect(score.value).toBeNull;
	});	

	it("should return error code 2 when question is not found", function() {
		var input = {
			"LFHC:9999":"1"
		}

		var score = calculateLCS(input,json);

		expect(score.error).toBe('2');
		expect(score.value).toBeNull;
	});	

});