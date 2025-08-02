import os
import json
import requests
from langsmith import Client

# Load test tickets
test_tickets_path = os.path.join(os.path.dirname(__file__), "test_tickets.json")
with open(test_tickets_path, "r") as f:
    test_tickets = json.load(f)

# LangSmith client (requires LANGCHAIN_API_KEY env variable)
client = Client()

# Backend API endpoint (update if needed)
API_URL = "http://localhost:8080/api/analysis/analyze"

def evaluate_analysis_output(ticket, result):
    """
    Use LangSmith to evaluate the AI output for clarity, relevance, and actionability.
    """
    eval_prompt = f"""
    Evaluate the following AI-generated analysis for clarity, relevance, and actionability.\n\n"""
    eval_prompt += f"Analysis Summary: {result['report'].get('summary', '')}\n"
    eval_prompt += f"Gaps: {result['report'].get('gapsIdentified', '')}\n"
    eval_prompt += f"Recommendations: {result['report'].get('recommendations', '')}\n"
    eval_prompt += f"Regression Areas: {result['report'].get('regressionAreas', '')}\n"
    eval_prompt += f"Related Tickets: {result['report'].get('relatedTickets', '')}\n"

    run = client.create_run(
        name="ImpactLens Output Quality Test",
        inputs=ticket,
        outputs=result,
        tags=["output-quality"]
    )

    score = client.evaluate(
        run_id=run.id,
        prompt=eval_prompt,
        criteria=["clarity", "relevance", "actionability"]
    )
    return score

def main():
    for ticket in test_tickets:
        print(f"\nEvaluating ticket: {ticket['ticketId']}")
        try:
            response = requests.post(API_URL, json=ticket)
            response.raise_for_status()
            result = response.json()
            score = evaluate_analysis_output(ticket, result)
            print("Evaluation Score:", score)
        except Exception as e:
            print(f"Error evaluating ticket {ticket['ticketId']}: {e}")

if __name__ == "__main__":
    main()
