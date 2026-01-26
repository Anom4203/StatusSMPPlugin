package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusTab implements TabCompleter {

    private final List<String> tiers = Arrays.asList("iron", "diamond", "netherite", "clear");
    private final List<String> booleanValues = Arrays.asList("true", "false");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();
        String cmd = command.getName().toLowerCase();
        String input = args.length == 0 ? "" : args[args.length - 1].toLowerCase();

        // Helper: online players
        List<String> onlinePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(p.getName());
        }

        switch (cmd) {

            /* ---------------- setStatus ----------------
             * /setStatus <player|self> <tier>
             * /setStatus <tier>   (self)
             */
            case "setstatus" -> {
                if (args.length == 1) {
                    // First arg: player names, self, OR tier
                    for (String name : onlinePlayers) {
                        if (name.toLowerCase().startsWith(input))
                            completions.add(name);
                    }
                    if ("self".startsWith(input)) completions.add("self");

                    for (String tier : tiers) {
                        if (tier.startsWith(input))
                            completions.add(tier);
                    }
                }
                else if (args.length == 2) {
                    // Second arg: tiers only
                    for (String tier : tiers) {
                        if (tier.startsWith(input))
                            completions.add(tier);
                    }
                }
            }

            /* ---------------- showStatus ----------------
             * /showStatus <player>
             */
            case "showstatus" -> {
                if (args.length == 1) {
                    for (String name : onlinePlayers) {
                        if (name.toLowerCase().startsWith(input))
                            completions.add(name);
                    }
                }
            }

            /* ---------------- allowSpears ----------------
             * /allowSpears <player> <true|false>
             */
            case "allowspears" -> {
                if (args.length == 1) {
                    for (String name : onlinePlayers) {
                        if (name.toLowerCase().startsWith(input))
                            completions.add(name);
                    }
                }
                else if (args.length == 2) {
                    for (String bool : booleanValues) {
                        if (bool.startsWith(input))
                            completions.add(bool);
                    }
                }
            }

            /* ---------------- setMaces ----------------
             * /setMaces <number>
             */
            case "setmaces" -> {
                if (args.length == 1) {
                    completions.add("<amount>");
                }
            }

            /* ---------------- SetMaceDmgLimit ----------------
             * /SetMaceDmgLimit <double>
             */
            case "setmacedmglimit" -> {
                if (args.length == 1) {
                    completions.add("<damageCap>");
                }
            }

            /* ---------------- statusSmpSettings ----------------
             * /statusSmpSettings <player>
             */
            case "statussmpsettings" -> {
                if (args.length == 1) {
                    for (String name : onlinePlayers) {
                        if (name.toLowerCase().startsWith(input))
                            completions.add(name);
                    }
                }
            }
        }

        return completions;
    }
}
