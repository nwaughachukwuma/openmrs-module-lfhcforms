describe("Living Conditions Score Calculation", function() {

    it("Expect Livin Condition Score to be 3", function() {
    	var input = {
    		"LFHC:1320":"1",
    		"LFHC:1321":"LFHC:1401"
    	}

    	var score = calculateLCS(input,"livingConditions.js");

    	expect(score).toBe(3);
    });
});