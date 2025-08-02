# AI Output Quality Evaluation for ImpactLens

This folder contains scripts and configuration for evaluating the quality of AI-generated analysis results using LangSmith.

## Contents
- `evaluate_output.py`: Script to send test tickets, collect AI outputs, and evaluate them using LangSmith.
- `test_tickets.json`: Example test tickets for evaluation.
- `requirements.txt`: Python dependencies for running the evaluation.

## Usage
1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
2. Set your LangSmith API key as an environment variable:
   ```bash
   set LANGCHAIN_API_KEY=your-key-here
   ```
3. Run the evaluation script:
   ```bash
   python evaluate_output.py
   ```

Edit `test_tickets.json` to add or modify test cases as needed.
