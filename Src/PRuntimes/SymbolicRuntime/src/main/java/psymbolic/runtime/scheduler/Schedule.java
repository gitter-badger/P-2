package psymbolic.runtime.scheduler;

import lombok.Getter;
import psymbolic.runtime.machine.Machine;
import psymbolic.valuesummary.Guard;
import psymbolic.valuesummary.ListVS;
import psymbolic.valuesummary.PrimitiveVS;
import psymbolic.valuesummary.ValueSummary;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Schedule {

    private Guard filter = Guard.constTrue();

    public void restrictFilter(Guard c) { filter = filter.and(c); }
    public Guard getFilter() { return filter; }
    public void resetFilter() { filter = Guard.constTrue(); }

    public class Choice {
        @Getter
        PrimitiveVS<Machine> repeatSender = new PrimitiveVS<>();
        @Getter
        PrimitiveVS<Boolean> repeatBool = new PrimitiveVS<>();
        @Getter
        PrimitiveVS<Integer> repeatInt = new PrimitiveVS<>();
        @Getter
        PrimitiveVS<ValueSummary> repeatElement = new PrimitiveVS<>();
        @Getter
        List<PrimitiveVS<Machine>> backtrackSender = new ArrayList<>();
        @Getter
        List<PrimitiveVS<Boolean>> backtrackBool = new ArrayList();
        @Getter
        List<PrimitiveVS<Integer>> backtrackInt = new ArrayList<>();
        @Getter
        List<PrimitiveVS<ValueSummary>> backtrackElement = new ArrayList<>();

        public Choice() {
        }

        public Guard getRepeatUniverse() {
            return repeatSender.getUniverse().or(repeatBool.getUniverse().or(repeatInt.getUniverse().or(repeatElement.getUniverse())));
        }

        public boolean isRepeatEmpty() {
            return getRepeatUniverse().isFalse();
        }

        public boolean isBacktrackEmpty() {
            return backtrackSender.isEmpty() && backtrackBool.isEmpty() && backtrackInt.isEmpty() && backtrackElement.isEmpty();
        }

        public Choice restrict(Guard pc) {
            Choice c = new Choice();
            c.repeatSender = repeatSender.restrict(pc);
            c.repeatBool = repeatBool.restrict(pc);
            c.repeatInt = repeatInt.restrict(pc);
            c.repeatElement = repeatElement.restrict(pc);
            c.backtrackSender = backtrackSender.stream().map(x -> x.restrict(pc)).collect(Collectors.toList());
            c.backtrackBool = backtrackBool.stream().map(x -> x.restrict(pc)).collect(Collectors.toList());
            c.backtrackInt = backtrackInt.stream().map(x -> x.restrict(pc)).collect(Collectors.toList());
            c.backtrackElement = backtrackElement.stream().map(x -> x.restrict(pc)).collect(Collectors.toList());
            return c;
        }

        public void addRepeatSender(PrimitiveVS<Machine> choice) {
            repeatSender = choice;
        }

        public void addRepeatBool(PrimitiveVS<Boolean> choice) {
            repeatBool = choice;
        }

        public void addRepeatInt(PrimitiveVS<Integer> choice) {
            repeatInt = choice;
        }

        public void addRepeatElement(PrimitiveVS<ValueSummary> choice) {
            repeatElement = choice;
        }

        public void clearRepeat() {
            repeatSender = new PrimitiveVS<>();
            repeatBool = new PrimitiveVS<>();
            repeatInt = new PrimitiveVS<>();
            repeatElement = new PrimitiveVS<>();
        }

        public void addBacktrackSender(PrimitiveVS<Machine> choice) { backtrackSender.add(choice); }

        public void addBacktrackBool(PrimitiveVS<Boolean> choice) { backtrackBool.add(choice); }

        public void addBacktrackInt(PrimitiveVS<Integer> choice) { backtrackInt.add(choice); }

        public void addBacktrackElement(PrimitiveVS<ValueSummary> choice) { backtrackElement.add(choice); }

        public void clearBacktrack() {
            backtrackSender = new ArrayList<>();
            backtrackBool = new ArrayList<>();
            backtrackInt = new ArrayList<>();
            backtrackElement = new ArrayList<>();
        }
    }

    private List<Choice> choices = new ArrayList<>();

    public Choice getChoice(int d) {
        return choices.get(d);
    }

    public void clearChoice(int d) {
        choices.get(d).clearRepeat();
        choices.get(d).clearBacktrack();
    }


    public int getNumBacktracks() {
        int count = 0;
        for (Choice backtrack : choices) {
            if (!backtrack.isBacktrackEmpty()) count++;
        }
        return count;
    }

    public void addRepeatSender(PrimitiveVS<Machine> choice, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        choices.get(depth).addRepeatSender(choice);
    }

    public void addRepeatBool(PrimitiveVS<Boolean> choice, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        choices.get(depth).addRepeatBool(choice);
    }

    public void addRepeatInt(PrimitiveVS<Integer> choice, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        choices.get(depth).addRepeatInt(choice);
    }

    public void addRepeatElement(PrimitiveVS<ValueSummary> choice, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        choices.get(depth).addRepeatElement(choice);
    }

    public void addBacktrackSender(List<PrimitiveVS<Machine>> machines, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        for (PrimitiveVS<Machine> choice : machines) {
            choices.get(depth).addBacktrackSender(choice);
        }
    }

    public void addBacktrackBool(List<PrimitiveVS<Boolean>> bools, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        for (PrimitiveVS<Boolean> choice : bools) {
            choices.get(depth).addBacktrackBool(choice);
        }
    }

    public void addBacktrackInt(List<PrimitiveVS<Integer>> ints, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        for (PrimitiveVS<Integer> choice : ints) {
            choices.get(depth).addBacktrackInt(choice);
        }
    }

    public void addBacktrackElement(List<PrimitiveVS<ValueSummary>> elements, int depth) {
        if (depth >= choices.size()) {
            choices.add(new Choice());
        }
        for (PrimitiveVS<ValueSummary> choice : elements) {
            choices.get(depth).addBacktrackElement(choice);
        }
    }

    public PrimitiveVS<Machine> getRepeatSender(int depth) {
        return choices.get(depth).getRepeatSender();
    }

    public PrimitiveVS<Boolean> getRepeatBool(int depth) {
        return choices.get(depth).getRepeatBool();
    }

    public PrimitiveVS<Integer> getRepeatInt(int depth) {
        return choices.get(depth).getRepeatInt();
    }

    public PrimitiveVS<ValueSummary> getRepeatElement(int depth) {
        return choices.get(depth).getRepeatElement();
    }

    public List<PrimitiveVS<Machine>> getBacktrackSender(int depth) {
        return choices.get(depth).getBacktrackSender();
    }

    public List<PrimitiveVS<Boolean>> getBacktrackBool(int depth) {
        return choices.get(depth).getBacktrackBool();
    }

    public List<PrimitiveVS<Integer>> getBacktrackInt(int depth) {
        return choices.get(depth).getBacktrackInt();
    }

    public List<PrimitiveVS<ValueSummary>> getBacktrackElement(int depth) {
        return choices.get(depth).getBacktrackElement();
    }

    public void clearRepeat(int depth) {
        choices.get(depth).clearRepeat();
    }

    public void clearBacktrack(int depth) {
        choices.get(depth).clearBacktrack();
    }

    public int size() {
        return choices.size();
    }

    private Map<Class<? extends Machine>, ListVS<PrimitiveVS<Machine>>> createdMachines = new HashMap<>();
    private Set<Machine> machines = new HashSet<>();

    private Guard pc = Guard.constTrue();

    public Schedule() {
    }

    private Schedule(List<Choice> choices,
                     Map<Class<? extends Machine>, ListVS<PrimitiveVS<Machine>>> createdMachines,
                     Set<Machine> machines,
                     Guard pc) {
        this.choices = new ArrayList<>(choices);
        this.createdMachines = new HashMap<>(createdMachines);
        this.machines = new HashSet<>(machines);
        this.pc = pc;
    }

    public Set<Machine> getMachines() {
        return machines;
    }

    public Schedule guard(Guard pc) {
        List<Choice> newChoices = new ArrayList<>();
        for (Choice c : choices) {
            newChoices.add(c.restrict(pc));
        }
        return new Schedule(newChoices, createdMachines, machines, pc);
    }

    public Schedule removeEmptyRepeat() {
        List<Choice> newChoices = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            if (!choices.get(i).isRepeatEmpty()) {
                newChoices.add(choices.get(i));
            }
        }
        return new Schedule(newChoices, createdMachines, machines, pc);
    }

    public void makeMachine(Machine m, Guard pc) {
        PrimitiveVS<Machine> toAdd = new PrimitiveVS<>(m).restrict(pc);
        if (createdMachines.containsKey(m.getClass())) {
            createdMachines.put(m.getClass(), createdMachines.get(m.getClass()).add(toAdd));
        } else {
            createdMachines.put(m.getClass(), new ListVS<PrimitiveVS<Machine>>(Guard.constTrue()).add(toAdd));
        }
        machines.add(m);
    }

    public boolean hasMachine(Class<? extends Machine> type, PrimitiveVS<Integer> idx, Guard otherPc) {
        if (!createdMachines.containsKey(type)) return false;
        // TODO: may need fixing
        //ScheduleLogger.log("has machine of type");
        //ScheduleLogger.log(idx + " in range? " + createdMachines.get(type).inRange(idx).getGuard(false));
        if (!createdMachines.get(type).inRange(idx).getGuardFor(false).isFalse()) return false;
        PrimitiveVS<Machine> machines = createdMachines.get(type).get(idx);
        return !machines.restrict(pc).restrict(otherPc).getUniverse().isFalse();
    }

    public PrimitiveVS<Machine> getMachine(Class<? extends Machine> type, PrimitiveVS<Integer> idx) {
        PrimitiveVS<Machine> machines = createdMachines.get(type).get(idx);
        return machines.restrict(pc);
    }

    public Schedule getSingleSchedule() {
        Guard pc = Guard.constTrue();
        for (Choice choice : choices) {
            Choice guarded = choice.restrict(pc);
            PrimitiveVS<Machine> sender = guarded.getRepeatSender();
            if (sender.getGuardedValues().size() > 0) {
                pc = pc.and(sender.getGuardedValues().get(0).getGuard());
            } else {
                PrimitiveVS<Boolean> boolChoice = guarded.getRepeatBool();
                if (boolChoice.getGuardedValues().size() > 0) {
                    pc = pc.and(boolChoice.getGuardedValues().get(0).getGuard());
                } else {
                    PrimitiveVS<Integer> intChoice = guarded.getRepeatInt();
                    if (intChoice.getGuardedValues().size() > 0) {
                        pc = pc.and(intChoice.getGuardedValues().get(0).getGuard());
                    }
                    else {
                        PrimitiveVS<ValueSummary> elementChoice = guarded.getRepeatElement();
                        if (elementChoice.getGuardedValues().size() > 0) {
                            pc = pc.and(elementChoice.getGuardedValues().get(0).getGuard());
                        }
                    }
                }
            }
        }
        return this.guard(pc).removeEmptyRepeat();
    }

    public Guard getLengthCond(int size) {
        if (size == 0) return Guard.constFalse();
        return choices.get(size - 1).getRepeatUniverse();
    }

}
