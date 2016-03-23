describe("Living Conditions Score Calculation", function() {

	var json;

	beforeEach(function () {
		json = {
			"name":"lcsDefinitionTable",
			"description":"",
			"questions": 
			[
			{
				"conceptMapping":"LFHC:1000",
				"scores":
				[
				{
					"score": "0",
					"value":"4",
				},
				{
					"score":"1",
					"low": "0",
					"high":"3"
				},
				{
					"score":"2",
					"value":"5",
					"operator":">="
				}
				]
			},
			{
				"conceptMapping":"LFHC:1001",
				"scores":
				[
				{
					"score":"0",
					"conceptMapping":"LFHC:1100"
				},
				{
					"score": "1",
					"conceptMapping":"LFHC:1101"
				},
				{
					"score": "2",
					"conceptMapping":"LFHC:1102"
				}
				]
			}
			]
		};
	});


	it("should do basic score calculation", function() {
		var input = {
			"LFHC:1000":"1",
			"LFHC:1001":"LFHC:1101"
		}

		var score = calculateLCS(input,json);

		expect(score.value).toBe(2);
	});

	it("should handle range ('high' and 'low' properties)", function() {
		var input = {
			"LFHC:1000":"2"
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
		expect(score.value).toBe(1);
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