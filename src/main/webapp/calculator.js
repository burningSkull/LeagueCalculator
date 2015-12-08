function initGroup(group) {
    for (var i = 0; i < group.players.length; i++) {
        var pA = group.players[i];
        for (var j = 0; j < group.players.length; j++) {
            var pB = group.players[j];
            if (pA.name != pB.name) {
                var found = false;
                for (var k = 0; k < group.matches.length; k++) {
                    var m = group.matches[k];
                    if ((m.p1 == pA.name && m.p2 == pB.name) || (m.p1 == pB.name && m.p2 == pA.name)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    var m = new Object();
                    m.p1 = pA.name;
                    m.p2 = pB.name;
                    m.actualResult = MATCH_NOT_PLAYED;
                    group.matches.push(m);
                }
            }
        }
    }
}

function resetPlayers(group) {
    group.players.forEach(function (p) {
        p.matchesPlayed = 0;
        p.points = 0;
        p.average = "-";
        p._2nd = 0;
        p._3rd = 0;
    });
}

function resetMatches(group) {
    group.matches.forEach(function (m) {
        m.speculativeResult = m.actualResult;
    });
}

function calculatePlayersAttributes(group) {
    for (var i = 0; i < group.players.length; i++) {
        var p = group.players[i];
        for (var j = 0; j < group.matches.length; j++) {
            var m = group.matches[j];
            if (m.p1 == p.name || m.p2 == p.name) {
                if (m.speculativeResult != MATCH_NOT_PLAYED) {
                    p.points += ((m.p1 == p.name) ? m.speculativeResult : (GAMES_PER_MATCH - m.speculativeResult)) * 1;
                    p.matchesPlayed++;
                    p.average = p.matchesPlayed == 0 ? "-" : p.points / p.matchesPlayed;
                }
            }
        }
    }

    for (var i = 0; i < group.players.length; i++) {
        var p = group.players[i];

        for (var j = 0; j < group.players.length; j++) {
            if (j != i && p.points == group.players[j].points) {
                for (var k = 0; k < group.matches.length; k++) {
                    var m = group.matches[k];
                    if (m.speculativeResult != MATCH_NOT_PLAYED &&
                            ((m.p1 == p.name && m.p2 == group.players[j].name) ||
                                    (m.p2 == p.name && m.p1 == group.players[j].name))) {
                        p._2nd += ((m.p1 == p.name) ? m.speculativeResult : (GAMES_PER_MATCH - group.matches[k].speculativeResult)) * 1;
                        break;
                    }
                }
            }
        }

        for (var j = 0; j < group.matches.length; j++) {
            var m = group.matches[j];
            if (m.p1 == p.name || m.p2 == p.name) {
                if (m.speculativeResult != MATCH_NOT_PLAYED) {
                    var oppName = (m.p1 == p.name) ? m.p2 : m.p1;
                    var opp;
                    for (var k = 0; k < group.players.length; k++) {
                        if (group.players[k].name == oppName) {
                            opp = group.players[k];
                            break;
                        }
                    }
                    p._3rd += ((m.p1 == p.name) ? m.speculativeResult : (GAMES_PER_MATCH - m.speculativeResult)) * opp.points;
                }
            }
        }
    }
}

function calculatePlayersRanks(group) {
    var currentRank = 1;
    group.players[0].rank = 1;
    for (var i = 1; i < group.players.length; i++) {
        group.players[i].rank = (comparePlayers(group.players[i], group.players[i - 1], true) == 0 ? currentRank : ++currentRank);
    }
}

function comparePlayers(p1, p2, noAlphabet) {
    if (p1.points != p2.points) {
        if (p1.points == "-")
            return 1;
        else if (p2.points == "-")
            return -1;
        else
            return p2.points - p1.points;
    } else {
        if (p1._2nd != p2._2nd) {
            return p2._2nd - p1._2nd;
        } else {
            if (p1._3rd != p2._3rd || noAlphabet) {
                return p2._3rd - p1._3rd;
            } else {
                return p1.name > p2.name ? 1 : -1;
            }
        }
    }
}

function sortPlayers(group) {
    group.players = group.players.sort(comparePlayers);
}

function findPlayerByName(group, name) {
    for (var i = 0; i <= group.players.length; i++) {
        if (group.players[i].name == name) {
            return group.players[i];
        }
    }
}

function sortMatches(group) {
    group.matches.forEach(function (m) {
        if (comparePlayers(findPlayerByName(group, m.p1), findPlayerByName(group, m.p2), false) > 0) {
            var swap = m.p1;
            m.p1 = m.p2;
            m.p2 = swap;
            if (m.actualResult != MATCH_NOT_PLAYED) {
                m.actualResult = GAMES_PER_MATCH - m.actualResult;
            }
            if (m.speculativeResult != MATCH_NOT_PLAYED) {
                m.speculativeResult = GAMES_PER_MATCH - m.speculativeResult;
            }
        }
    });

    group.matches = group.matches.sort(function (a, b) {
        if (a.speculativeResult == MATCH_NOT_PLAYED && b.speculativeResult != MATCH_NOT_PLAYED) {
            return 1;
        }
        if (b.speculativeResult == MATCH_NOT_PLAYED && a.speculativeResult != MATCH_NOT_PLAYED) {
            return -1;
        }

        if (a.p1 != b.p1) {
            return comparePlayers(findPlayerByName(group, a.p1), findPlayerByName(group, b.p1), false);
        } else {
            return comparePlayers(findPlayerByName(group, a.p2), findPlayerByName(group, b.p2), false);
        }
    });
}