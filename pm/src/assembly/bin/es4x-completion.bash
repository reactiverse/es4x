#!/usr/bin/env bash
_es4x_completions()
{
  COMPREPLY=($(es4x --compgen ${2}))
}

complete -F _es4x_completions es4x
