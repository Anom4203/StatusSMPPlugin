package com.example.orestatus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusTab implements TabCompleter {

    private final List<String> tiers = Arrays.asList("iron", "diamond", "netherite", "clear");
    private final List<String> booleanValues = Arrays.asList("true", "false");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String cmdName = command.getName().toLowerCase();
        String input = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
        
        if (args.length == 1) {
            // For setStatus: suggest player names, "self", or tiers
            if (cmdName.equals("setstatus")) {
                // Check if input matches a tier (for setStatus when used as /setStatus <tier>)
                for (String tier : tiers) {
                    if (tier.toLowerCase().startsWith(input)) {
                        completions.add(tier);
                    }
                }
                if ("self".startsWith(input)) {
                    completions.add("self");
                }
                // Also suggest player names
                sender.getServer().getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(input))
                        completions.add(p.getName());
                });
            }
            // For showStatus: suggest player names
            else if (cmdName.equals("showstatus")) {
                sender.getServer().getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(input))
                        completions.add(p.getName());
                });
            }
            // For allowSpears: suggest player names
            else if (cmdName.equals("allowspears")) {
                sender.getServer().getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(input))
                        completions.add(p.getName());
                });
            }
            // For setMaces: no suggestions (numbers are arbitrary)
            // Return empty list
        } else if (args.length == 2) {
            // For setStatus with player specified, suggest tiers
            if (cmdName.equals("setstatus")) {
                for (String tier : tiers) {
                    if (tier.toLowerCase().startsWith(input)) {
                        completions.add(tier);
                    }
                }
            }
            // For allowSpears: suggest true/false
            else if (cmdName.equals("allowspears")) {
                for (String bool : booleanValues) {
                    if (bool.toLowerCase().startsWith(input)) {
                        completions.add(bool);
                    }
                }
            }
        }
        
        return completions;
    }
}
