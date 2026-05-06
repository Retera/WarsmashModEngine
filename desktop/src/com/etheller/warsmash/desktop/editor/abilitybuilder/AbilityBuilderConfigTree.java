package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;

public class AbilityBuilderConfigTree extends JTree {
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode extraTargetConditionsNode;
	private DefaultMutableTreeNode extraAutoTargetConditionsNode;
	private DefaultMutableTreeNode extraAutoNoTargetConditionsNode;
	private DefaultMutableTreeNode extraCastConditionsNode;
	private DefaultMutableTreeNode onAddAbilityNode;
	private DefaultMutableTreeNode onAddDisabledAbilityNode;
	private DefaultTreeModel defaultTreeModel;

	public AbilityBuilderConfigTree(DefaultTreeModel defaultTreeModel) {
		super(defaultTreeModel);
		this.defaultTreeModel = defaultTreeModel;
		root = new DefaultMutableTreeNode();

		root.add(extraTargetConditionsNode = createConditionsNode("Conditions - Extra Target"));
		root.add(extraAutoTargetConditionsNode = createConditionsNode("Conditions - Extra Aura Target"));
		root.add(extraAutoNoTargetConditionsNode = createConditionsNode("Conditions - Extra Aura No Target"));
		root.add(extraCastConditionsNode = createConditionsNode("Conditions - Extra Cast"));

		root.add(onAddAbilityNode = createActionsNode("Actions - On Add Ability"));
		root.add(onAddDisabledAbilityNode = createActionsNode("Actions - On Add Disabled Ability"));
		root.add(createActionsNode("Actions - On Remove Ability"));
		root.add(createActionsNode("Actions - On Remove Disabled Ability"));
		root.add(createActionsNode("Actions - On Death Pre Cast"));
		root.add(createActionsNode("Actions - On Cancel Pre Cast"));
		root.add(createActionsNode("Actions - On Order Issued"));
		root.add(createActionsNode("Actions - On Activate"));
		root.add(createActionsNode("Actions - On Deactivate"));
		root.add(createActionsNode("Actions - On Level Change"));
		root.add(createActionsNode("Actions - On Begin Casting"));
		root.add(createActionsNode("Actions - On End Casting"));
		root.add(createActionsNode("Actions - On Channel Tick"));
		root.add(createActionsNode("Actions - On End Channel"));
	}

	private DefaultMutableTreeNode createConditionsNode(String name) {
		DefaultMutableTreeNode conditionTreeNode = new DefaultMutableTreeNode(name);
		return conditionTreeNode;
	}

	private DefaultMutableTreeNode createActionsNode(String name) {
		DefaultMutableTreeNode conditionTreeNode = new DefaultMutableTreeNode(name);
		return conditionTreeNode;
	}

	public void setConfig(ABAbilityBuilderConfiguration config) {
		root = new DefaultMutableTreeNode(config.getId() + " \"" + config.getCastId() + "\"");

		Method[] declaredMethods = ABAbilityBuilderConfiguration.class.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			String name = declaredMethod.getName();
			if (name.startsWith("getOn")) {
				String fixedName = fixActionsName(name);
				DefaultMutableTreeNode actionsNode = createActionsNode(fixedName);
				try {
					List<ABAction> actions = (List<ABAction>) declaredMethod.invoke(config, new Object[0]);
					if (actions != null) {
						root.add(actionsNode);
						for (ABAction action : actions) {
							generateInto(actionsNode, action);
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if (name.startsWith("getExtra")) {
				String fixedName = fixConditionsName(name);
				DefaultMutableTreeNode conditionsNode = createConditionsNode(fixedName);
				try {
					List<ABBooleanCallback> conditions = (List<ABBooleanCallback>) declaredMethod.invoke(config, new Object[0]);
					if (conditions != null) {
						root.add(conditionsNode);
						for (ABBooleanCallback condition : conditions) {
							generateInto(conditionsNode, condition);
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		defaultTreeModel.setRoot(root);

		int index = 0;
		while (index < getRowCount()) {
			expandRow(index++);
		}
	}

	public void generateInto(DefaultMutableTreeNode actionsNode, Object action) {
		Class<?> clazz = action.getClass();
		DefaultMutableTreeNode nodeForAction = new DefaultMutableTreeNode(describe(action));
		// generate child nodes
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (List.class.isAssignableFrom(field.getType())) {
				try {
					DefaultMutableTreeNode nodeForField = new DefaultMutableTreeNode(field.getName());
					nodeForAction.add(nodeForField);
					List fieldValue = (List) field.get(action);
					if (fieldValue != null) {
						for (int i = 0; i < fieldValue.size(); i++) {
							Object listItem = fieldValue.get(i);
							if (listItem != null) {
								generateInto(nodeForField, listItem);
							}
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if (ABAction.class.isAssignableFrom(field.getType())
					|| ABBooleanCallback.class.isAssignableFrom(field.getType())) {
				try {
					DefaultMutableTreeNode nodeForField = new DefaultMutableTreeNode(field.getName());
					nodeForAction.add(nodeForField);
					Object fieldValue = field.get(action);
					if (fieldValue != null) {
						generateInto(nodeForField, fieldValue);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		actionsNode.add(nodeForAction);
	}

	private Object describe(Object action) {
		if (action == null) {
			return null;
		}
		if (action instanceof String) {
			return action.toString();
		}
		if (action instanceof Number) {
			return action.toString();
		}
		if (action instanceof Enum) {
			return action.toString();
		}
		if (action instanceof List) {
			return "[...]";
		}
		Class<?> clazz = action.getClass();
		try {
			Method toStringMethod = clazz.getDeclaredMethod("toString");
			if (toStringMethod != null) {
				return action.toString();
			}
		} catch (NoSuchMethodException | SecurityException e) {
		}
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getSimpleName());
		sb.append("{");
		Field[] fields = clazz.getDeclaredFields();
		boolean first = true;
		for (Field field : fields) {
			if (!ABAction.class.isAssignableFrom(field.getType())
					&& !ABBooleanCallback.class.isAssignableFrom(field.getType())) {
				String fieldName = field.getName();
				if ("recycleRect".equals(fieldName)) {
					continue;
				}
				field.setAccessible(true);
				if (!first) {
					sb.append(", ");
				}
				sb.append(fieldName);
				sb.append("=");
				try {
					Object fieldValue = field.get(action);
					sb.append(describe(fieldValue));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				first = false;
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public void clearConfig() {

	}

	private String fixActionsName(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name.charAt(3));
		for (int i = 4; i < name.length(); i++) {
			char nameChar = name.charAt(i);
			if (Character.isUpperCase(nameChar)) {
				sb.append(' ');
			}
			sb.append(nameChar);
		}
		return "Actions - " + sb.toString();
	}

	private String fixConditionsName(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name.charAt(3));
		for (int i = 4; i < name.length(); i++) {
			char nameChar = name.charAt(i);
			if (Character.isUpperCase(nameChar)) {
				sb.append(' ');
			}
			sb.append(nameChar);
		}
		return "Conditions - " + sb.toString();
	}

	public static AbilityBuilderConfigTree create() {
		return new AbilityBuilderConfigTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
	}
}
