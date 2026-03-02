#!/bin/sh
while true
do
  opencode --model=openai/gpt-5.3-codex run "Please pick an unimplemented card Utils/who_issue_tracker.txt, implement it, and mark it as done afterwards. When done, create a git commit named: [<Set>] Implement '<Card Name>'. Cherry-pick this commit to another branch named: <Set>-<CardName>."
  git checkout with-agents-and-cards
  sleep 2
done
