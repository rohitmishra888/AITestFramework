import os
import json
from langsmith import Client

# Load mock responses
data_path = os.path.join(os.path.dirname(__file__), "mock_responses.json")
with open(data_path, "r") as f:
    mock_responses = json.load(f)

client = Client(api_key="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")

def evaluate_analysis_output(ticket_id, result):
    eval_prompt = f"""
    Evaluate the following AI-generated analysis for clarity, relevance, and actionability.\n\n"""
    eval_prompt += f"Analysis Summary: {result['report'].get('summary', '')}\n"
    eval_prompt += f"Gaps: {result['report'].get('gapsIdentified', '')}\n"
    eval_prompt += f"Recommendations: {result['report'].get('recommendations', '')}\n"
    eval_prompt += f"Regression Areas: {result['report'].get('regressionAreas', '')}\n"
    eval_prompt += f"Related Tickets: {result['report'].get('relatedTickets', '')}\n"

    run = client.create_run(
        name="ImpactLens Output Quality Test (Mock)",
        run_type="tool",
        inputs={"ticketId": ticket_id},
        outputs=result,
        tags=["output-quality", "mock"]
    )
    if run is None:
        print(f"Error: create_run() returned None. This usually means authentication failed or the API key is missing/invalid.")
        return None

    score = client.evaluate(
        run_id=run.id,
        prompt=eval_prompt,
        criteria=["clarity", "relevance", "actionability"]
    )
    return score

def main():
    for mock in mock_responses:
        ticket_id = mock["ticketId"]
        result = mock["response"]
        print(f"\nEvaluating mock ticket: {ticket_id}")
        try:
            score = evaluate_analysis_output(ticket_id, result)
            print("Evaluation Score:", score)
        except Exception as e:
            print(f"Error evaluating ticket {ticket_id}: {e}")

if __name__ == "__main__":
    main()
